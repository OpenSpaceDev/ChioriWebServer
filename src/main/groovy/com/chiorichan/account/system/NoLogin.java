/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.account.system;

import com.chiorichan.account.adapter.AccountLookupAdapter;
import com.chiorichan.account.adapter.memory.MemoryAccount;
import com.chiorichan.account.lang.LoginException;

public class NoLogin extends MemoryAccount
{
	public NoLogin() throws LoginException
	{
		super( "noLogin", AccountLookupAdapter.MEMORY_ADAPTER );
	}
}
