/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2015 Chiori Greene a.k.a. Chiori-chan <me@chiorichan.com>
 * All Right Reserved.
 */
package com.chiorichan.event;

public class TimedRegisteredListener extends RegisteredListener
{
	private int count;
	private long totalTime;
	private Class<? extends Event> eventClass;
	private boolean multiple = false;
	
	public TimedRegisteredListener( final Listener pluginListener, final EventExecutor eventExecutor, final EventPriority eventPriority, final EventCreator registeredPlugin, final boolean listenCancelled )
	{
		super( pluginListener, eventExecutor, eventPriority, registeredPlugin, listenCancelled );
	}
	
	@Override
	public void callEvent( Event event ) throws EventException
	{
		if ( event.isAsynchronous() )
		{
			super.callEvent( event );
			return;
		}
		count++;
		Class<? extends Event> newEventClass = event.getClass();
		if ( this.eventClass == null )
		{
			this.eventClass = newEventClass;
		}
		else if ( !this.eventClass.equals( newEventClass ) )
		{
			multiple = true;
			this.eventClass = getCommonSuperclass( newEventClass, this.eventClass ).asSubclass( Event.class );
		}
		long start = System.nanoTime();
		super.callEvent( event );
		totalTime += System.nanoTime() - start;
	}
	
	private static Class<?> getCommonSuperclass( Class<?> class1, Class<?> class2 )
	{
		while ( !class1.isAssignableFrom( class2 ) )
		{
			class1 = class1.getSuperclass();
		}
		return class1;
	}
	
	/**
	 * Resets the call count and total time for this listener
	 */
	public void reset()
	{
		count = 0;
		totalTime = 0;
	}
	
	/**
	 * Gets the total times this listener has been called
	 * 
	 * @return Times this listener has been called
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Gets the total time calls to this listener have taken
	 * 
	 * @return Total time for all calls of this listener
	 */
	public long getTotalTime()
	{
		return totalTime;
	}
	
	/**
	 * Gets the class of the events this listener handled. If it handled multiple classes of event, the closest shared
	 * superclass will be returned, such that for any event this listener has handled, <code>this.getEventClass().isAssignableFrom(event.getClass())</code> and no class <code>this.getEventClass().isAssignableFrom(clazz)
	 * && this.getEventClass() != clazz &&
	 * event.getClass().isAssignableFrom(clazz)</code> for all handled events.
	 * 
	 * @return the event class handled by this RegisteredListener
	 */
	public Class<? extends Event> getEventClass()
	{
		return eventClass;
	}
	
	/**
	 * Gets whether this listener has handled multiple events, such that for some two events, <code>eventA.getClass() != eventB.getClass()</code>.
	 * 
	 * @return true if this listener has handled multiple events
	 */
	public boolean hasMultiple()
	{
		return multiple;
	}
}
