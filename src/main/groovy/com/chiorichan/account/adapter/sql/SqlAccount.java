/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.account.adapter.sql;

import java.sql.SQLException;

import com.chiorichan.account.Account;
import com.chiorichan.account.AccountMetaData;
import com.chiorichan.account.LoginException;
import com.chiorichan.account.LoginExceptionReason;
import com.chiorichan.util.CommonFunc;

public class SqlAccount extends Account
{
	protected final SqlAdapter lookupAdapter;
	
	public SqlAccount( AccountMetaData meta, SqlAdapter adapter ) throws LoginException
	{
		super( meta );
		
		if ( adapter == null )
			throw new LoginException( LoginExceptionReason.unknownError );
		
		lookupAdapter = adapter;
	}
	
	@Override
	public void preLoginCheck() throws LoginException
	{
		if ( metaData.getInteger( "numloginfail" ) > 5 )
			if ( metaData.getInteger( "lastloginfail" ) > ( CommonFunc.getEpoch() - 1800 ) )
				throw new LoginException( LoginExceptionReason.underAttackPleaseWait );
		
		if ( !metaData.getString( "actnum" ).equals( "0" ) )
			throw new LoginException( LoginExceptionReason.accountNotActivated );
	}
	
	@Override
	public void postLoginCheck() throws LoginException
	{
		try
		{
			lookupAdapter.sql.queryUpdate( "UPDATE `accounts` SET `lastActive` = '" + CommonFunc.getEpoch() + "', `lastLogin` = '" + CommonFunc.getEpoch() + "', `lastLoginFail` = 0, `numLoginFail` = 0 WHERE `accountID` = '" + getAcctId() + "'" );
		}
		catch ( SQLException e )
		{
			throw new LoginException( e );
		}
	}
	
	@Override
	public boolean isYou( String id )
	{
		for ( String f : lookupAdapter.accountFields )
			if ( metaData.getString( f ) != null && metaData.getString( f ).equals( id ) )
				return true;
		
		return false;
	}
	
	@Override
	public String getPassword()
	{
		return getString( "password" );
	}
	
	@Override
	public String getDisplayName()
	{
		return ( getString( "fname" ).isEmpty() ) ? getString( "name" ) : getString( "fname" ) + " " + getString( "name" );
	}
	
	@Override
	public String getUsername()
	{
		return getString( "username" );
	}
	
	@Override
	public boolean isValid()
	{
		return metaData.hasMinimumData();
	}
	
	@Override
	public SqlAdapter getLookupAdapter()
	{
		return null;
	}
}