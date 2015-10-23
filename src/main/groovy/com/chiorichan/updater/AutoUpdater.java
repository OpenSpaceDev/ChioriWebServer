/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.updater;

import java.util.ArrayList;
import java.util.List;

import com.chiorichan.LogColor;
import com.chiorichan.Loader;
import com.chiorichan.account.AccountAttachment;
import com.chiorichan.account.event.AccountSuccessfulLoginEvent;
import com.chiorichan.event.EventBus;
import com.chiorichan.event.EventHandler;
import com.chiorichan.event.EventPriority;
import com.chiorichan.event.Listener;
import com.chiorichan.messaging.MessageReceiver;
import com.chiorichan.tasks.TaskCreator;
import com.chiorichan.tasks.TaskManager;
import com.chiorichan.tasks.Timings;
import com.chiorichan.updater.BuildArtifact.ChangeSet.ChangeSetDetails;
import com.chiorichan.util.Versioning;

public class AutoUpdater implements Listener, TaskCreator
{
	public static final String WARN_CONSOLE = "warn-console";
	public static final String WARN_OPERATORS = "warn-ops";
	
	private static AutoUpdater instance = null;
	private final DownloadUpdaterService service;
	private final List<String> onUpdate = new ArrayList<String>();
	private final List<String> onBroken = new ArrayList<String>();
	private final String channel;
	private boolean enabled;
	private BuildArtifact current = null;
	private BuildArtifact latest = null;
	private boolean suggestChannels = true;
	
	public AutoUpdater( DownloadUpdaterService service, String channel )
	{
		instance = this;
		this.service = service;
		this.channel = channel;
		
		/*
		 * This schedules the Auto Updater with the Scheduler to run every 30 minutes (by default).
		 */
		TaskManager.INSTANCE.scheduleAsyncRepeatingTask( this, 0L, Loader.getConfig().getInt( "auto-updater.check-interval", 30 ) * Timings.TICK_MINUTE, new Runnable()
		{
			@Override
			public void run()
			{
				check();
			}
		} );
	}
	
	protected static DownloadUpdaterService getService()
	{
		return ( instance == null ) ? null : instance.service;
	}
	
	public void check()
	{
		check( Loader.getConsole(), true );
	}
	
	public void check( final AccountAttachment sender, final boolean automatic )
	{
		final String currentSlug = Versioning.getBuildNumber();
		
		if ( !isEnabled() || "0".equals( currentSlug ) )
			return;
		
		if ( !sender.getEntity().checkPermission( "sys.update" ).isTrue() && !sender.getEntity().isOp() )
			return;
		
		new Thread()
		{
			@Override
			public void run()
			{
				current = service.getArtifact( currentSlug, "information about this version; perhaps you are running a custom one?" );
				latest = service.getArtifact( "lastStableBuild", "latest artifact information" );
				
				try
				{
					if ( isUpdateAvailable() )
					{
						if ( ( current.isBroken() ) && ( onBroken.contains( WARN_CONSOLE ) ) )
						{
							sender.sendMessage( LogColor.RED + "----- Chiori Auto Updater -----" );
							sender.sendMessage( LogColor.RED + "Your version of " + Versioning.getProduct() + " is known to be broken. It is strongly advised that you update to a more recent version ASAP." );
							sender.sendMessage( LogColor.RED + "Known issues with your version:" );
							
							for ( String line : current.getBrokenReason().split( "\n" ) )
								sender.sendMessage( LogColor.RED + "> " + line );
							
							sender.sendMessage( LogColor.RED + "Newer version " + latest.getVersion() + " (build #" + latest.getBuildNumber() + ") was released on " + latest.getCreated() + "." );
							sender.sendMessage( LogColor.RED + "Details: " + latest.getHtmlUrl() );
							sender.sendMessage( LogColor.RED + "Download: " + latest.getJar() );
							sender.sendMessage( LogColor.RED + "----- ------------------- -----" );
						}
						else if ( onUpdate.contains( WARN_CONSOLE ) )
						{
							sender.sendMessage( LogColor.YELLOW + "----- Chiori Auto Updater -----" );
							sender.sendMessage( LogColor.YELLOW + "Your version of " + Versioning.getProduct() + " is out of date. Version " + latest.getVersion() + " (build #" + latest.getBuildNumber() + ") was released on " + latest.getCreated() + "." );
							sender.sendMessage( LogColor.YELLOW + "Details: " + latest.getHtmlUrl() );
							sender.sendMessage( LogColor.YELLOW + "Download: " + latest.getJar() );
							sender.sendMessage( LogColor.YELLOW + "----- ------------------- -----" );
						}
					}
					else if ( ( current != null ) && ( current.isBroken() ) && ( onBroken.contains( WARN_CONSOLE ) ) )
					{
						sender.sendMessage( LogColor.RED + "----- Chiori Auto Updater -----" );
						sender.sendMessage( LogColor.RED + "Your version of " + Versioning.getProduct() + " is known to be broken. It is strongly advised that you update to a more recent (or older) version ASAP." );
						sender.sendMessage( LogColor.RED + "Known issues with your version:" );
						
						for ( String line : current.getBrokenReason().split( "\n" ) )
							sender.sendMessage( LogColor.RED + "> " + line );
						
						sender.sendMessage( LogColor.RED + "Unfortunately, there is not yet a newer version suitable for your server. We would advise you wait an hour or two, or try out a dev build." );
						sender.sendMessage( LogColor.RED + "----- ------------------- -----" );
					}
					else if ( current == null && latest != null )
					{
						sender.sendMessage( LogColor.YELLOW + "----- Chiori Auto Updater -----" );
						sender.sendMessage( LogColor.YELLOW + "It appears that we could not find any information regarding your current build of Chiori Web Server. This could either be due to your" );
						sender.sendMessage( LogColor.YELLOW + "version being so out of date that our Build Server has no information or you self compiled this build, in which case you should have disabled" );
						sender.sendMessage( LogColor.YELLOW + "the auto updates. For the sake of fair warning below is our latest release. Please run \"update latest\" if you like us to auto update." );
						sender.sendMessage( LogColor.YELLOW + "" );
						sender.sendMessage( LogColor.YELLOW + "Latest Version " + latest.getVersion() + " (build #" + latest.getBuildNumber() + ") was released on " + latest.getCreated() + "." );
						sender.sendMessage( LogColor.YELLOW + "Details: " + latest.getHtmlUrl() );
						sender.sendMessage( LogColor.YELLOW + "Download: " + latest.getJar() );
						sender.sendMessage( LogColor.YELLOW + "----- ------------------- -----" );
					}
					else if ( !automatic )
					{
						sender.sendMessage( LogColor.YELLOW + "----- Chiori Auto Updater -----" );
						
						if ( current == null && latest == null )
							sender.sendMessage( LogColor.YELLOW + "There seems to have been a problem checking for updates!" );
						else
							sender.sendMessage( LogColor.YELLOW + "You are already running the latest version of " + Versioning.getProduct() + "!" );
						
						sender.sendMessage( LogColor.YELLOW + "----- ------------------- -----" );
					}
				}
				catch ( Throwable t )
				{
					t.printStackTrace();
				}
			}
		}.start();
	}
	
	public void forceUpdate( final AccountAttachment sender )
	{
		new Thread()
		{
			@Override
			public void run()
			{
				current = ( Versioning.getBuildNumber().equals( "0" ) ) ? null : service.getArtifact( Versioning.getBuildNumber(), "information about this " + Versioning.getProduct() + " version; perhaps you are running a custom one?" );
				latest = service.getArtifact( "lastStableBuild", "latest artifact information" );
				
				if ( latest == null )
				{
					sender.sendMessage( LogColor.YELLOW + "----- Chiori Auto Updater -----" );
					sender.sendMessage( LogColor.YELLOW + "There seems to have been a problem checking for updates!" );
					sender.sendMessage( LogColor.YELLOW + "----- ------------------- -----" );
				}
				else
				{
					sender.sendMessage( LogColor.YELLOW + "----- Chiori Auto Updater -----" );
					
					if ( current != null )
						sender.sendMessage( LogColor.YELLOW + "Your Version " + current.getVersion() + " (build #" + current.getBuildNumber() + ") was released on " + current.getCreated() + "." );
					
					sender.sendMessage( LogColor.YELLOW + "Latest Version " + latest.getVersion() + " (build #" + latest.getBuildNumber() + ") was released on " + latest.getCreated() + "." );
					sender.sendMessage( LogColor.YELLOW + "Details: " + latest.getHtmlUrl() );
					sender.sendMessage( LogColor.YELLOW + "Download: " + latest.getJar() );
					sender.sendMessage( "" );
					
					for ( ChangeSetDetails l : latest.getChanges() )
						for ( String ll : l.toString().split( "\n" ) )
							sender.sendMessage( LogColor.AQUA + "[CHANGES] " + LogColor.WHITE + ll );
					
					sender.sendMessage( "" );
					sender.sendMessage( LogColor.YELLOW + "If you would like " + Versioning.getProduct() + " to update to the latest version run \"update latest force\"" );
					sender.sendMessage( LogColor.RED + "WARNING: Chiori Auto Updater currently can't auto update any installed plugins." );
					sender.sendMessage( LogColor.RED + "You can obtain updated offical plugins from the Details URL above or you will need to contact the original developer." );
					sender.sendMessage( LogColor.RED + "Quite frankly, If there has been no changes to the Plugin API (See Change Log) then even outdated plugins should still work." );
				}
			}
		}.start();
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public BuildArtifact getCurrent()
	{
		return current;
	}
	
	public BuildArtifact getLatest()
	{
		return latest;
	}
	
	@Override
	public String getName()
	{
		return "Auto Updater";
	}
	
	public List<String> getOnBroken()
	{
		return onBroken;
	}
	
	public List<String> getOnUpdate()
	{
		return onUpdate;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public boolean isUpdateAvailable()
	{
		if ( ( latest == null ) || ( current == null ) || ( !isEnabled() ) )
			return false;
		else
			return latest.timestamp > current.timestamp;
	}
	
	@EventHandler( priority = EventPriority.NORMAL )
	public void onAccountLoginEvent( AccountSuccessfulLoginEvent event )
	{
		if ( event.getAccountPermissible() instanceof MessageReceiver )
		{
			MessageReceiver receiver = ( MessageReceiver ) event.getAccountPermissible();
			if ( Loader.getAutoUpdater().isEnabled() && Loader.getAutoUpdater().getCurrent() != null && event.getAccount().getEntity().checkPermission( Loader.BROADCAST_CHANNEL_ADMINISTRATIVE ).isTrue() )
				if ( ( Loader.getAutoUpdater().getCurrent().isBroken() ) && ( getOnBroken().contains( AutoUpdater.WARN_OPERATORS ) ) )
					receiver.sendMessage( LogColor.DARK_RED + "The version of " + Versioning.getProduct() + " that this server is running is known to be broken. Please consider updating to the latest version available from http://jenkins.chiorichan.com/." );
				else if ( ( Loader.getAutoUpdater().isUpdateAvailable() ) && ( getOnUpdate().contains( AutoUpdater.WARN_OPERATORS ) ) )
					receiver.sendMessage( LogColor.DARK_PURPLE + "The version of " + Versioning.getProduct() + " that this server is running is out of date. Please consider updating to the latest version available from http://jenkins.chiorichan.com/." );
		}
	}
	
	public void setEnabled( boolean isEnabled )
	{
		enabled = isEnabled;
		if ( enabled )
			EventBus.INSTANCE.registerEvents( this, Loader.getInstance() );
		else
			EventBus.INSTANCE.unregisterEvents( this );
	}
	
	public void setSuggestChannels( boolean suggestChannels )
	{
		this.suggestChannels = suggestChannels;
	}
	
	public boolean shouldSuggestChannels()
	{
		return suggestChannels;
	}
}
