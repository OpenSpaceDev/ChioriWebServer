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

import com.chiorichan.permission.PermissibleGroup;

public class SQLGroup extends PermissibleGroup
{
	public SQLGroup( String id, SQLBackend sql )
	{
		super( id, sql );
	}
	
	@Override
	public void save()
	{
		
	}
	
	@Override
	public void remove()
	{
		
	}

	@Override
	public void reloadPermissions()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void reloadGroups()
	{
		// TODO Auto-generated method stub
	}
}