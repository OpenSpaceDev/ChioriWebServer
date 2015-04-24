/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.permission.backend.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.chiorichan.Loader;
import com.chiorichan.database.DatabaseEngine;
import com.chiorichan.permission.ChildPermission;
import com.chiorichan.permission.PermissibleEntityProxy;
import com.chiorichan.permission.PermissibleGroup;
import com.chiorichan.permission.Permission;
import com.chiorichan.permission.PermissionDefault;
import com.chiorichan.permission.PermissionNamespace;
import com.chiorichan.permission.PermissionValue;
import com.chiorichan.util.ObjectFunc;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class SQLEntity extends PermissibleEntityProxy
{
	public SQLEntity( String id, SQLBackend sql )
	{
		super( id, sql );
	}
	
	@Override
	public void reloadPermissions()
	{
		DatabaseEngine db = ( ( SQLBackend ) backend ).getSQL();
		
		detachAllPermissions();
		try
		{
			ResultSet rs = db.query( "SELECT * FROM `permissions_entity` WHERE `owner` = '" + getId() + "' AND `type` = '0';" );
			
			if ( rs.next() )
				do
				{
					PermissionNamespace ns = new PermissionNamespace( rs.getString( "permission" ) );
					
					List<Permission> perms = Permission.getNodes( ns );
					
					if ( perms.isEmpty() && !ns.containsRegex() )
						perms.add( Permission.getNode( ns.fixInvalidChars().getNamespace() ) );
					
					for ( Permission perm : perms )
					{
						if ( getChildPermission( perm.getNamespace() ) == null )
						{
							PermissionValue<?> childValue = ( rs.getString( "value" ) == null || rs.getString( "value" ).isEmpty() ) ? null : perm.getValue().createChild( rs.getString( "value" ) );
							attachPermission( new ChildPermission( perm, childValue, false, Splitter.on( "|" ).splitToList( rs.getString( "ref" ) ) ) );
						}
					}
				}
				while ( rs.next() );
			
			/*
			 * Adds the EVERYBODY Permission Node to all entities.
			 */
			Permission perm = PermissionDefault.EVERYBODY.getNode();
			attachPermission( new ChildPermission<Boolean>( perm, null, false, "" ) );
		}
		catch ( SQLException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public void reloadGroups()
	{
		DatabaseEngine db = ( ( SQLBackend ) backend ).getSQL();
		
		clearGroups();
		try
		{
			ResultSet rs = db.query( "SELECT * FROM `permissions_groups` WHERE `parent` = '" + getId() + "' AND `type` = '0';" );
			
			if ( rs.next() )
				do
				{
					PermissibleGroup grp = Loader.getPermissionManager().getGroup( rs.getString( "child" ) );
					groups.put( grp.getId(), grp );
				}
				while ( rs.next() );
		}
		catch ( SQLException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public void save()
	{
		DatabaseEngine db = ( ( SQLBackend ) backend ).getSQL();
		try
		{
			db.queryUpdate( "DELETE FROM `permissions_entity` WHERE `owner` = '" + getId() + "' AND `type` = '0';" );
			
			for ( ChildPermission cp : getChildPermissions() )
				db.queryUpdate( "INSERT INTO `permissions_entity` (`owner`,`type`,`ref`,`permission`,`value`) VALUES ('" + getId() + "','0','" + Joiner.on( "|" ).join( cp.getReferences() ) + "','" + cp.getPermission().getNamespace() + "','" + ObjectFunc.castToString( cp.getValue().getValue() ) + "');" );
		}
		catch ( SQLException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public void remove()
	{
		DatabaseEngine db = ( ( SQLBackend ) backend ).getSQL();
		try
		{
			db.queryUpdate( "DELETE FROM `permissions_entity` WHERE `owner` = '" + getId() + "' AND `type` = '0';" );
		}
		catch ( SQLException e )
		{
			throw new RuntimeException( e );
		}
	}
}