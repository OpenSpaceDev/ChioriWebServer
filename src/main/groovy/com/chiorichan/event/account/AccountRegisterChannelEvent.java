/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.event.account;

import com.chiorichan.account.AccountHandler;

/**
 * This is called immediately after a User registers for a plugin channel.
 */
public class AccountRegisterChannelEvent extends AccountChannelEvent
{
	public AccountRegisterChannelEvent( final AccountHandler acct, final String channel )
	{
		super( acct, channel );
	}
}