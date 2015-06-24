/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.permission;

import com.chiorichan.account.AccountType;
import com.chiorichan.permission.lang.PermissionException;
import com.chiorichan.permission.lang.PermissionValueException;
import com.chiorichan.tasks.Timings;
import com.chiorichan.util.ObjectFunc;
import com.chiorichan.util.StringFunc;

/**
 * Holds the union between {@link Permission} and {@link PermissibleEntity}<br>
 * Also provides access to {@link #assign(String...)} and {@link #assign(Object, String...)}
 */
public class PermissionResult
{
	public static final PermissionResult DUMMY = new PermissionResult( AccountType.ACCOUNT_NONE.getPermissibleEntity(), PermissionDefault.DEFAULT.getNode() );
	
	private ChildPermission childPerm = null;
	private final PermissibleEntity entity;
	private final Permission perm;
	private String ref;
	
	protected int timecode = Timings.epoch();
	
	PermissionResult( PermissibleEntity entity, Permission perm )
	{
		this( entity, perm, "" );
	}
	
	PermissionResult( PermissibleEntity entity, Permission perm, String ref )
	{
		ref = StringFunc.formatReference( ref );
		
		assert ( entity != null );
		assert ( perm != null );
		
		this.entity = entity;
		this.perm = perm;
		this.ref = ref;
		childPerm = entity.findChildPermission( perm, ref );
	}
	
	public PermissionResult assign( Object val, String... refs )
	{
		if ( perm.getType() == PermissionType.DEFAULT )
			throw new PermissionException( String.format( "Can't assign the permission %s with value %s to entity %s, because the permission is of default type, which can't carry a value other than assigned or not.", perm.getNamespace(), val, entity.getId(), perm.getType().name() ) );
		
		if ( val == null )
			throw new PermissionValueException( "The assigned value must not be null." );
		
		childPerm = new ChildPermission( perm, perm.getModel().createValue( val ), entity.isGroup() ? ( ( PermissibleGroup ) entity ).getWeight() : -1, refs );
		entity.childPermissions.add( childPerm );
		
		recalculatePermissions();
		
		return this;
	}
	
	public PermissionResult assign( String... refs )
	{
		if ( perm.getType() != PermissionType.DEFAULT )
			throw new PermissionException( String.format( "Can't assign the permission %s to entity %s, because the permission is of type %s, use assign(Object) with the appropriate value instead.", perm.getNamespace(), entity.getId(), perm.getType().name() ) );
		
		childPerm = new ChildPermission( perm, perm.getModel().createValue( true ), entity.isGroup() ? ( ( PermissibleGroup ) entity ).getWeight() : -1, refs );
		entity.childPermissions.add( childPerm );
		
		recalculatePermissions();
		
		return this;
	}
	
	/**
	 * See {@link Permission#commit()}<br>
	 * Caution: will commit changes made to other child values of the same permission node
	 * 
	 * @return The {@link PermissionResult} for chaining
	 */
	public PermissionResult commit()
	{
		perm.commit();
		entity.save();
		return this;
	}
	
	public PermissibleEntity getEntity()
	{
		return entity;
	}
	
	public int getInt()
	{
		return ObjectFunc.castToInt( getValue().getValue() );
	}
	
	public Permission getPermission()
	{
		return perm;
	}
	
	public String getReference()
	{
		return ref;
	}
	
	public String getString()
	{
		return ObjectFunc.castToString( getValue().getValue() );
	}
	
	public PermissionValue getValue()
	{
		if ( childPerm == null || childPerm.getValue() == null || !isAssigned() )
			return perm.getModel().getModelValue();
		
		return childPerm.getValue();
	}
	
	/**
	 * Returns a final object based on assignment of permission.
	 * 
	 * @return
	 *         Unassigned will return the default value.
	 */
	@SuppressWarnings( "unchecked" )
	public <T> T getValueObject()
	{
		Object obj;
		
		if ( isAssigned() )
		{
			if ( childPerm == null || childPerm.getValue() == null )
				obj = perm.getModel().getModelValue();
			else
				obj = childPerm.getObject();
		}
		else
			obj = perm.getModel().getValueDefault();
		
		try
		{
			return ( T ) obj;
		}
		catch ( ClassCastException e )
		{
			throw new PermissionValueException( String.format( "Can't cast %s to type", obj.getClass().getName() ), e );
		}
	}
	
	public int getWeight()
	{
		return childPerm == null ? 9999 : childPerm.getWeight();
	}
	
	/**
	 * @return was this entity assigned an custom value for this permission.
	 */
	public boolean hasValue()
	{
		return perm.getType() != PermissionType.DEFAULT && childPerm != null && childPerm.getValue() != null;
	}
	
	/**
	 * @return was this permission assigned to our entity?
	 */
	public boolean isAssigned()
	{
		return childPerm != null;
	}
	
	/**
	 * @return was this permission assigned to our entity thru a group? Will return false if not assigned.
	 */
	public boolean isInherited()
	{
		if ( !isAssigned() )
			return false;
		
		return childPerm.isInherited();
	}
	
	/**
	 * A safe version of isTrue() in case you don't care to know if the permission is of type Boolean or not
	 * 
	 * @return is this permission true
	 */
	public boolean isTrue()
	{
		try
		{
			return isTrueWithException();
		}
		catch ( PermissionException e )
		{
			return false;
		}
	}
	
	/**
	 * Used strictly for BOOLEAN permission nodes.
	 * 
	 * @return is this permission true
	 * @throws IllegalAccessException
	 *             Thrown if this permission node is not of type Boolean
	 */
	public boolean isTrueWithException() throws PermissionException
	{
		// Can't check true on anything but these types
		if ( perm.getType() != PermissionType.BOOL && perm.getType() != PermissionType.DEFAULT )
			throw new PermissionValueException( String.format( "The permission %s is not of type boolean.", perm.getNamespace() ) );
		
		// We can check and allow OPs but ONLY if we are not checking a PermissionDefault node, for one 'sys.op' is the node we check for OPs.
		if ( !PermissionDefault.isDefault( perm ) && PermissionManager.allowOps && entity.isOp() )
			return true;
		
		if ( perm.getType() == PermissionType.DEFAULT )
			return isAssigned();
		
		return ( getValueObject() == null ) ? false : ObjectFunc.castToBool( getValueObject() );
	}
	
	public PermissionResult recalculatePermissions()
	{
		return recalculatePermissions( "" );
	}
	
	public PermissionResult recalculatePermissions( String ref )
	{
		ref = StringFunc.formatReference( ref );
		this.ref = ref;
		childPerm = entity.findChildPermission( perm, ref );
		return this;
	}
	
	@Override
	public String toString()
	{
		return String.format( "PermissionResult{name=%s,value=%s,isAssigned=%s}", perm.getNamespace(), getValueObject(), isAssigned() );
	}
}
