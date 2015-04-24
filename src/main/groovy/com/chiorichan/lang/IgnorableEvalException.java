/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 */
package com.chiorichan.lang;

/**
 * Used to convey EVAL exceptions to handler but not being fatal
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
public class IgnorableEvalException extends Exception
{
	private static final long serialVersionUID = 8509074551067643277L;
	
	public IgnorableEvalException( String msg, Throwable t )
	{
		super( msg, t );
	}
}