/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.datastore.sql;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.chiorichan.lang.StartupException;

/**
 * 
 */
public class SQLiteDatastore extends SQLDatastore
{
	String connection;
	
	public void initSQLite( String filename ) throws SQLException
	{
		try
		{
			Class.forName( "org.sqlite.JDBC" );
		}
		catch ( ClassNotFoundException e )
		{
			throw new StartupException( "We could not locate the 'org.sqlite.JDBC' library, be sure to have this library in your build path." );
		}
		
		File sqliteDb = new File( filename );
		
		if ( !sqliteDb.exists() )
		{
			getLogger().warning( "The SQLite file '" + sqliteDb.getAbsolutePath() + "' did not exist, we will attempt to create a blank one now." );
			try
			{
				sqliteDb.createNewFile();
			}
			catch ( IOException e )
			{
				throw new SQLException( "We had a problem creating the SQLite file, the exact exception message was: " + e.getMessage(), e );
			}
		}
		
		connection = "jdbc:sqlite:" + sqliteDb.getAbsolutePath();
		
		attemptConnection( connection );
		
		getLogger().info( "We succesully connected to the sqLite database with connection string '" + connection + "'" );
	}
}
