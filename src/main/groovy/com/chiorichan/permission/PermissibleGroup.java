/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.permission;

import com.chiorichan.permission.event.PermissibleEntityEvent;

public abstract class PermissibleGroup extends PermissibleBase implements Comparable<PermissibleGroup>
{
	protected int weight = 0;
	
	public PermissibleGroup( String groupName )
	{
		super( groupName );
	}
	
	@Override
	public int compareTo( PermissibleGroup o )
	{
		return getWeight() - o.getWeight();
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public void setWeight( int weight )
	{
		this.weight = weight;
		PermissionManager.callEvent( new PermissibleEntityEvent( this, PermissibleEntityEvent.Action.WEIGHT_CHANGED ) );
	}
}
