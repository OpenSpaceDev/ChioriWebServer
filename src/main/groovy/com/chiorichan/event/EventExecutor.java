package com.chiorichan.event;

public interface EventExecutor
{
	public void execute( Listener listener, Event event ) throws EventException;
}
