/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 *
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 *
 * All Rights Reserved.
 */
package com.chiorichan.session;

/**
 * Method used to keep a Session persistent from request to request.
 * 
 * XXX This is an outdated feature from like version 6, not sure if it's still working as of version 9.
 */
public enum SessionPersistenceMethod
{
	COOKIE, PARAM
}
