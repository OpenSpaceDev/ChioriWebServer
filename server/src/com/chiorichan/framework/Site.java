package com.chiorichan.framework;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.chiorichan.Loader;
import com.chiorichan.database.SqlConnector;
import com.chiorichan.event.EventException;
import com.chiorichan.event.server.SiteLoadEvent;
import com.chiorichan.file.YamlConfiguration;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Site
{
	public String siteId, title, domain;
	File source, resource;
	Map<String, String> subdomains, aliases;
	Set<String> metatags = Sets.newHashSet(),
			protectedFiles = Sets.newHashSet();
	YamlConfiguration config;
	SqlConnector sql;
	
	@SuppressWarnings( "unchecked" )
	public Site(ResultSet rs) throws SiteException
	{
		try
		{
			Type mapType = new TypeToken<HashMap<String, String>>()
			{
			}.getType();
			
			siteId = rs.getString( "siteID" );
			title = rs.getString( "title" );
			domain = rs.getString( "domain" );
			
			Loader.getLogger().info( "Loading site '" + siteId + "' with title '" + title + "' from Framework Database." );
			
			Gson gson = new GsonBuilder().create();
			try
			{
				if ( !rs.getString( "protected" ).isEmpty() )
					protectedFiles.addAll( gson.fromJson( new JSONObject( rs.getString( "protected" ) ).toString(), HashSet.class ) );
			}
			catch ( Exception e )
			{
				Loader.getLogger().warning( "MALFORMED JSON EXPRESSION for 'protected' field for site '" + siteId + "'" );
			}
			
			String sources = rs.getString( "source" );
			
			if ( sources == null || sources.isEmpty() )
			{
				source = getAbsoluteRoot();
			}
			else if ( sources.startsWith( "." ) )
			{
				source = new File( getAbsoluteRoot() + sources );
			}
			else
			{
				source = new File( getAbsoluteRoot(), sources );
				protectedFiles.add( "/" + sources );
			}
			
			if ( source.isFile() )
				source.delete();
			
			if ( !source.exists() )
				source.mkdirs();
			
			String resources = rs.getString( "resource" );
			
			if ( resources == null || resources.isEmpty() )
			{
				resource = getAbsoluteRoot();
			}
			else if ( resources.startsWith( "." ) )
			{
				resource = new File( getAbsoluteRoot() + resources );
			}
			else
			{
				resource = new File( getAbsoluteRoot(), resources );
				protectedFiles.add( "/" + resources );
			}
			
			if ( resource.isFile() )
				resource.delete();
			
			if ( !resource.exists() )
				resource.mkdirs();
			
			try
			{
				if ( !rs.getString( "metatags" ).isEmpty() )
					metatags.addAll( gson.fromJson( new JSONObject( rs.getString( "metatags" ) ).toString(), HashSet.class ) );
			}
			catch ( Exception e )
			{
				Loader.getLogger().warning( "MALFORMED JSON EXPRESSION for 'metatags' field for site '" + siteId + "'" );
			}
			
			try
			{
				if ( !rs.getString( "aliases" ).isEmpty() )
					aliases = gson.fromJson( new JSONObject( rs.getString( "aliases" ) ).toString(), mapType );
			}
			catch ( Exception e )
			{
				Loader.getLogger().warning( "MALFORMED JSON EXPRESSION for 'aliases' field for site '" + siteId + "'" );
			}
			
			try
			{
				if ( !rs.getString( "subdomains" ).isEmpty() )
					subdomains = gson.fromJson( new JSONObject( rs.getString( "subdomains" ) ).toString(), mapType );
			}
			catch ( Exception e )
			{
				Loader.getLogger().warning( "MALFORMED JSON EXPRESSION for 'subdomains' field for site '" + siteId + "'" );
			}
			
			try
			{
				String yaml = rs.getString( "configYaml" );
				InputStream is = new ByteArrayInputStream( yaml.getBytes( "ISO-8859-1" ) );
				config = YamlConfiguration.loadConfiguration( is );
			}
			catch ( Exception e )
			{
				Loader.getLogger().warning( "MALFORMED YAML EXPRESSION for 'configYaml' field for site '" + siteId + "'" );
				config = new YamlConfiguration();
			}
			
			if ( config != null && config.getConfigurationSection( "database" ) != null )
			{
				String type = config.getString( "database.type" );
				
				String host = config.getString( "database.host" );
				String port = config.getString( "database.port" );
				String database = config.getString( "database.database" );
				String username = config.getString( "database.username" );
				String password = config.getString( "database.password" );
				
				String filename = config.getString( "database.filename" );
				
				sql = new SqlConnector();
				
				try
				{
					if ( type.equalsIgnoreCase( "mysql" ) )
						sql.init( database, username, password, host, port );
					else if ( type.equalsIgnoreCase( "sqlite" ) )
						sql.init( filename );
					else
						Loader.getLogger().severe( "The SqlConnector for site '" + siteId + "' can not support anything other then mySql or sqLite at the moment. Please change 'database.type' in the site config to 'mysql' or 'sqLite' and set the connection params." );
				}
				catch ( SQLException e )
				{
					if ( e.getCause() instanceof ConnectException )
						Loader.getLogger().severe( "We had a problem connecting to database '" + database + "'. Reason: " + e.getCause().getMessage() );
					else
						Loader.getLogger().severe( e.getMessage() );
					
					return;
				}
				finally
				{
					Loader.getLogger().info( "Successfully connected to site database for site `" + siteId + "`" );
				}
			}
			
			SiteLoadEvent event = new SiteLoadEvent( this );
			
			Loader.getPluginManager().callEventWithException( event );
			
			if ( event.isCancelled() )
				throw new SiteException( "Site loading was cancelled by an internal event." );
		}
		catch ( SQLException | EventException e )
		{
			throw new SiteException( e );
		}
	}
	
	public YamlConfiguration getYaml()
	{
		if ( config == null )
			config = new YamlConfiguration();
		
		return config;
	}
	
	public Set<String> getMetatags()
	{
		if ( metatags == null )
			return new HashSet<String>();
		
		return metatags;
	}
	
	public Map<String, String> getAliases()
	{
		return aliases;
	}
	
	public Site(String id, String title0, String domain0)
	{
		siteId = id;
		title = title0;
		domain = domain0;
		protectedFiles = new HashSet<String>();
		metatags = Sets.newHashSet();
		aliases = Maps.newLinkedHashMap();
		subdomains = Maps.newLinkedHashMap();
		
		source = getAbsoluteRoot();
		resource = new File( getAbsoluteRoot(), "resource" );
		
		if ( !source.exists() )
			source.mkdirs();
		
		if ( !resource.exists() )
			resource.mkdirs();
	}
	
	public boolean protectCheck( String file )
	{
		if ( protectedFiles == null )
			return false;
		
		// Is the file being checked belong to our webroot
		if ( file.startsWith( getRoot() ) )
		{
			// Strip our webroot from file
			file = file.substring( getRoot().length() );
			
			for ( String n : protectedFiles )
			{
				if ( n != null && !n.isEmpty() )
				{
					// If the length is greater then 1 and file starts with this string.
					if ( n.length() > 1 && file.startsWith( n ) )
						return true;
					
					// Does our file end with this. ie: .php, .txt, .etc
					if ( file.endsWith( n ) )
						return true;
					
					// If the pattern does not start with a /, see if name contain this string.
					if ( !n.startsWith( "/" ) && file.contains( n ) )
						return true;
					
					// Lastly try the string as a RegEx pattern
					if ( file.matches( n ) )
						return true;
				}
			}
		}
		
		return false;
	}
	
	public File getAbsoluteRoot()
	{
		return getAbsoluteRoot( null );
	}
	
	public File getAbsoluteRoot( String subdomain )
	{
		File target = new File( Loader.webroot, getRoot( subdomain ) );
		
		if ( target.isFile() )
			target.delete();
		
		if ( !target.exists() )
			target.mkdirs();
		
		return target;
	}
	
	public String getRoot()
	{
		return getRoot( null );
	}
	
	public String getRoot( String subdomain )
	{
		String target = siteId;
		
		if ( subdomains != null && subdomain != null && !subdomain.isEmpty() )
		{
			String sub = subdomains.get( subdomain );
			
			if ( sub != null )
				target = siteId + "/" + sub;
		}
		
		return target;
	}
	
	public SqlConnector getDatabase()
	{
		return sql;
	}
	
	public String applyAlias( String source )
	{
		if ( aliases == null || aliases.size() < 1 )
			return source;
		
		for ( Entry<String, String> entry : aliases.entrySet() )
		{
			source = source.replace( "%" + entry.getKey() + "%", entry.getValue() );
		}
		
		return source;
	}
	
	public String getName()
	{
		return siteId;
	}
	
	public File getResourceDirectory()
	{
		if ( resource == null )
			resource = new File( getAbsoluteRoot(), "resource" );
		
		return resource;
	}
	
	public File getSourceDirectory()
	{
		if ( source == null )
			source = getAbsoluteRoot();
		
		return source;
	}
	
	public void setAutoSave( boolean b )
	{
		// TODO Auto-generated method stub
	}
	
	// TODO: Add methods to add protected files, metatags and aliases to site and save
}
