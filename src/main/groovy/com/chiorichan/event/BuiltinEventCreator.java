/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * Copyright 2015 Chiori-chan. All Right Reserved.
 * 
 * @author Chiori Greene
 * @email chiorigreene@gmail.com
 */
package com.chiorichan.event;

import com.chiorichan.configuration.file.YamlConfiguration;
import com.chiorichan.lang.InvalidDescriptionException;
import com.chiorichan.plugin.PluginDescriptionFile;

public abstract class BuiltinEventCreator implements EventCreator
{
	private YamlConfiguration yaml = new YamlConfiguration();
	
	public final PluginDescriptionFile getDescription()
	{
		try
		{
			return new PluginDescriptionFile( yaml );
		}
		catch ( InvalidDescriptionException e )
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isEnabled()
	{
		return true;
	}
}