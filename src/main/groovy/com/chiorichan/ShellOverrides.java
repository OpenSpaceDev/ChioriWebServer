/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.chiorichan.logger.Log;
import com.google.common.collect.Maps;

/**
 * Loads ShellOverrides from shells.properties file.
 * This file is used to override what Interpreter will handle a particular file extension.
 */
public class ShellOverrides
{
	static Map<String, String> interpreters = Maps.newLinkedHashMap();

	static
	{
		try
		{
			File contentTypes = new File( AppConfig.get().getDirectory().getAbsolutePath(), "shells.properties" );

			if ( !contentTypes.exists() )
				contentTypes.createNewFile();

			InputStream isDefault = Loader.class.getClassLoader().getResourceAsStream( "com/chiorichan/shells.properties" );
			InputStream is = new FileInputStream( contentTypes );
			try
			{
				Properties prop = new Properties();
				prop.load( isDefault );
				prop.load( is );
				for ( Object o : prop.keySet() )
					if ( o instanceof String )
						interpreters.put( ( String ) o, ( String ) prop.get( o ) );
			}
			finally
			{
				if ( is != null )
					is.close();
			}
		}
		catch ( IOException e )
		{
			Log.get().severe( "Could not load the InterpreterOverride properties file, exact error was: " + e.getMessage() );
		}
	}

	public static String getFileExtension( File file )
	{
		return getFileExtension( file.getName() );
	}

	public static String getFileExtension( String file )
	{
		try
		{
			String[] extension = file.split( "\\." );
			return extension[extension.length - 1];
		}
		catch ( Throwable t )
		{
			return "";
		}
	}

	public static String getShellForExt( String ext )
	{
		if ( ext.isEmpty() )
			return null;

		if ( interpreters != null && interpreters.containsKey( ext.toLowerCase() ) )
			return interpreters.get( ext.toLowerCase() );
		else
			return null;
	}

	public static String getShellForFile( File file )
	{
		String ext = getFileExtension( file ).toLowerCase();

		if ( ext.isEmpty() )
			return null;

		if ( interpreters != null && interpreters.containsKey( ext ) )
			return interpreters.get( ext );
		else
			return null;
	}
}
