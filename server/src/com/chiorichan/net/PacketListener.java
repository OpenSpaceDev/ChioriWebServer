package com.chiorichan.net;

import com.chiorichan.ChatColor;
import com.chiorichan.Loader;
import com.chiorichan.event.net.TCPConnectedEvent;
import com.chiorichan.event.net.TCPDisconnectedEvent;
import com.chiorichan.event.net.TCPIdleEvent;
import com.chiorichan.event.net.TCPIncomingEvent;
import com.chiorichan.net.packet.CommandPacket;
import com.chiorichan.net.packet.PingPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;

public class PacketListener extends PacketManager
{
	protected String lastPingId = "";
	
	/*
	 * public void sendPing()
	 * {
	 * lastPingId = Common.md5( System.currentTimeMillis() + "" );
	 * Loader.getTcpServer().send.sendPacket( new PingPacket( lastPingId, System.currentTimeMillis() ) );
	 * }
	 */
	
	public PacketListener(Kryo kryo)
	{
		super.registerApiPackets( kryo );
	}
	
	@Override
	public void received( Connection var1, Object var2 )
	{
		if ( var2 instanceof BasePacket )
		{	
			
		}
		else if ( var2 instanceof Packet )
		{
			Loader.getLogger().info( "&5Got packet '" + var2 + "' from client at '" + var1.getRemoteAddressTCP().getAddress().getHostAddress() + "'" );
			
			boolean handled = true;
			
			( (Packet) var2 ).received( var1 );
			
			if ( var2 instanceof PingPacket )
			{
				PingPacket ping = (PingPacket) var2;
				if ( ping.isReply )
				{
					if ( ping.id == lastPingId )
					{
						long localDelay = System.currentTimeMillis() - ping.created;
						long outDelay = ping.created - ping.received;
						long inDelay = System.currentTimeMillis() - ping.received;
						
						System.out.println( "Network Latency Report: Round Trip " + localDelay + ", Outbound Trip " + outDelay + ", Inbound Trip " + inDelay );
					}
				}
				else
				{
					ping.isReply = true;
					ping.received = System.currentTimeMillis();
					var1.sendTCP( ping );
				}
			}
			else if ( var2 instanceof CommandPacket )
			{
				CommandPacket var3 = ( (CommandPacket) var2 );
				
				switch ( var3.getKeyword().toUpperCase() )
				{
					case "PING":
						Loader.getLogger().info( ChatColor.NEGATIVE + "&2 Received a ping from the client: " + var3.getPayload() + "ms " );
						var1.sendTCP( new CommandPacket( "PONG", System.currentTimeMillis() ) );
				}
			}
			else
			{
				handled = false;
			}
			
			TCPIncomingEvent event = new TCPIncomingEvent( var1, (Packet) var2, handled );
			Loader.getPluginManager().callEvent( event );
		}
		else if ( var2 instanceof KeepAlive )
		{
			// KryoNet KeepAlive Packet
		}
		else if ( var2 instanceof Ping )
		{
			// KryoNet Ping Packet
		}
		else
		{
			Loader.getLogger().severe( "We received an incoming packet from the server but we can't process it because it was not an instance of Packet. (" + var2.getClass() + ")" );
		}
	}
	
	@Override
	public void connected( Connection var1 )
	{
		TCPConnectedEvent event = new TCPConnectedEvent( var1 );
		Loader.getPluginManager().callEvent( event );
		if ( event.isCancelled() )
		{
			Loader.getLogger().info( ChatColor.YELLOW + "Connection from " + var1.getRemoteAddressTCP().getAddress().getHostAddress() + " was disconnected because it was concelled by the TCPConnectedEvent!" );
			var1.close();
		}
	}
	
	@Override
	public void disconnected( Connection var1 )
	{
		Loader.getPluginManager().callEvent( new TCPDisconnectedEvent( var1 ) );
	}
	
	@Override
	public void idle( Connection var1 )
	{
		Loader.getPluginManager().callEvent( new TCPIdleEvent( var1 ) );
	}
}