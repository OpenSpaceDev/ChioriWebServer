/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.event;

public class RegisteredListener
{
	private final Listener listener;
	private final EventPriority priority;
	private final EventCreator creator;
	private final EventExecutor executor;
	private final boolean ignoreCancelled;
	
	public RegisteredListener( final Listener listener, final EventExecutor executor, final EventPriority priority, final EventCreator creator, final boolean ignoreCancelled )
	{
		this.listener = listener;
		this.priority = priority;
		this.creator = creator;
		this.executor = executor;
		this.ignoreCancelled = ignoreCancelled;
	}
	
	/**
	 * Gets the listener for this registration
	 * 
	 * @return Registered Listener
	 */
	public Listener getListener()
	{
		return listener;
	}
	
	/**
	 * Gets the plugin for this registration
	 * 
	 * @return Registered Plugin
	 */
	public EventCreator getCreator()
	{
		return creator;
	}
	
	/**
	 * Gets the priority for this registration
	 * 
	 * @return Registered Priority
	 */
	public EventPriority getPriority()
	{
		return priority;
	}
	
	/**
	 * Calls the event executor
	 * 
	 * @param event
	 *            The event
	 * @throws EventException
	 *             If an event handler throws an exception.
	 */
	public void callEvent( final Event event ) throws EventException
	{
		if ( event instanceof Cancellable )
		{
			if ( ( ( Cancellable ) event ).isCancelled() && isIgnoringCancelled() )
			{
				return;
			}
		}
		executor.execute( listener, event );
	}
	
	/**
	 * Whether this listener accepts cancelled events
	 * 
	 * @return True when ignoring cancelled events
	 */
	public boolean isIgnoringCancelled()
	{
		return ignoreCancelled;
	}
}