package com.chiorichan.http;

import groovy.lang.Binding;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.chiorichan.Loader;
import com.chiorichan.database.SqlConnector;
import com.chiorichan.framework.ConfigurationManagerWrapper;
import com.chiorichan.framework.Evaling;
import com.chiorichan.framework.Framework;
import com.chiorichan.framework.HttpUtilsWrapper;
import com.chiorichan.framework.ServerUtilsWrapper;
import com.chiorichan.framework.UserServiceWrapper;
import com.chiorichan.user.User;
import com.chiorichan.util.Common;
import com.chiorichan.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * This class is used to carry data that is to be persistent from request to request.
 * If you need to sync data across requests then we recommend using Session Vars for Security.
 * 
 * @author Chiori Greene
 * @copyright Greenetree LLC
 */
public class PersistentSession
{
	protected Map<String, String> data = new LinkedHashMap<String, String>();
	protected int expires = 0, defaultLife = 86400000; // 1 Day!
	protected int timeout = 0, requestCnt = 0, defaultTimeout = 10000; // 10 minutes
	protected String candyId = "", candyName = "candyId";
	protected Candy sessionCandy;
	protected Binding binding = new Binding();
	protected Evaling eval;
	protected User currentUser = null;
	
	protected Map<String, Candy> candies = new LinkedHashMap<String, Candy>();
	protected Framework framework = null;
	protected HttpRequest request;
	protected Boolean stale = false;
	
	/**
	 * Returns an instance of Framework relevant to this session. Instigates a new one if not already done.
	 * 
	 * @return Framework
	 */
	public Framework getFramework()
	{
		if ( framework == null )
			framework = new Framework( this );
		
		binding.setVariable( "chiori", framework );
		
		return framework;
	}
	
	/**
	 * Initializes a new session based on the supplied HttpRequest.
	 * 
	 * @param _request
	 */
	protected PersistentSession(HttpRequest _request)
	{
		setRequest( _request, false );
	}
	
	protected void setRequest( HttpRequest _request, Boolean _stale )
	{
		request = _request;
		stale = _stale;
		
		rearmTimeout();
		
		if ( request.getSite().getYaml() != null )
			candyName = request.getSite().getYaml().getString( "sessions.cookie-name", candyName );
		
		candies = pullCandies( _request );
		sessionCandy = candies.get( candyName );
		
		initSession();
		
		binding.setVariable( "chiori", null );
		binding.setVariable( "request", request );
		binding.setVariable( "response", request.getResponse() );
		binding.setVariable( "__FILE__", new File( "" ) );
	}
	
	protected void handleUserProtocols()
	{
		String username = request.getArgument( "user" );
		String password = request.getArgument( "pass" );
		String target = request.getArgument( "target" );
		
		if ( request.getArgument( "logout", "", true ) != null )
		{
			logoutUser();
			
			if ( target.isEmpty() )
				target = request.getSite().getYaml().getString( "scripts.login-form", "/login" );
			
			Loader.getLogger().debug( target );
			
			request.getResponse().sendRedirect( target + "?ok=You have been successfully logged out. Please come again!" );
			return;
		}
		
		if ( !username.isEmpty() && !password.isEmpty() )
		{
			User user = request.getSite().getUserList().attemptLogin( this, username, password );
			
			Loader.getLogger().info( "User: " + user );
			
			if ( user != null && user.isValid() )
			{
				currentUser = user;
				
				String loginPost = ( target.isEmpty() ) ? request.getSite().getYaml().getString( "scripts.login-post", "/panel" ) : target;
				
				Loader.getLogger().info( "Login Success: Username \"" + username + "\", Password \"" + password + "\", UserId \"" + user.getUserId() + "\", Display Name \"" + user.getDisplayName() + "\", Display Level \"" + user.getDisplayLevel() + "\"" );
				request.getResponse().sendRedirect( loginPost );
			}
			else if ( user == null )
			{
				return;
			}
			else
			{
				String loginForm = request.getSite().getYaml().getString( "scripts.login-form", "/login" );
				
				Loader.getLogger().warning( "Login Failed: Username \"" + username + "\", Password \"" + password + "\", UserId \"" + user.getUserId() + "\", Display Name \"" + user.getDisplayName() + "\", Display Level \"" + user.getDisplayLevel() + "\"" );
				request.getResponse().sendRedirect( loginForm + "?msg=" + user.getLastError() + "&target=" + target );
			}
		}
		else if ( currentUser == null )
		{
			username = getArgument( "user" );
			password = getArgument( "pass" );
			
			if ( !username.isEmpty() && !password.isEmpty() )
			{
				User user = request.getSite().getUserList().attemptLogin( this, username, password );
				
				if ( user != null && user.isValid() )
				{
					currentUser = user;
					
					String loginPost = ( target == null || target.isEmpty() ) ? request.getSite().getYaml().getString( "scripts.login-post", "/panel" ) : target;
					
					Loader.getLogger().info( "Login Success: Username \"" + username + "\", Password \"" + password + "\", UserId \"" + user.getUserId() + "\", Display Name \"" + user.getDisplayName() + "\", Display Level \"" + user.getDisplayLevel() + "\"" );
					// _sess.getServer().dummyRedirect( loginPost );
				}
				else
				{
					Loader.getLogger().warning( "Login Status: No Valid Login Present" );
				}
			}
			else
				currentUser = null;
		}
		else
		{
			// Recheck validity of user login since possibly we are reusing a session.
		}
	}
	
	/**
	 * Performs a permission check against the currently logged in user but with a much more detailed result.
	 */
	public ReqFailureReason doReqCheck( String reqLevel )
	{
		// -1 = Allow All | 0 = Operator
		if ( reqLevel != null && !reqLevel.equals( "-1" ) )
		{
			if ( currentUser == null || !currentUser.isValid() )
			{
				return ReqFailureReason.NO_USER;
			}
			else if ( reqLevel.equals( "0" ) && !currentUser.hasPermission( "" ) ) // Root Check
			{
				return ReqFailureReason.OP_ONLY;
			}
			else if ( currentUser.hasPermission( reqLevel ) && !currentUser.getUserLevel().equals( "0" ) )
			{
				ReqFailureReason result = ReqFailureReason.NO_ACCESS;
				result.setReason( "This page is limited to users with access to the \"" + reqLevel + "\" permission or better." );
				return result;
			}
		}
		
		return ReqFailureReason.ACCEPTED;
	}
	
	public void setGlobal( String key, Object val )
	{
		binding.setVariable( key, val );
	}
	
	public Object getGlobal( String key )
	{
		return binding.getVariable( key );
	}
	
	public Map<String, Object> getGlobals()
	{
		return binding.getVariables();
	}
	
	protected Binding getBinding()
	{
		return binding;
	}
	
	protected void initSession()
	{
		SqlConnector sql = Loader.getPersistenceManager().getSql();
		
		if ( sessionCandy != null )
		{
			ResultSet rs = null;
			try
			{
				rs = sql.query( "SELECT * FROM `sessions` WHERE `sessid` = '" + sessionCandy.getValue() + "'" );
			}
			catch ( SQLException e1 )
			{
				e1.printStackTrace();
			}
			
			candyId = sessionCandy.getValue();
			
			if ( rs == null || sql.getRowCount( rs ) < 1 )
				sessionCandy = null;
			else
			{
				try
				{
					expires = rs.getInt( "expires" );
					data = new Gson().fromJson( rs.getString( "data" ), Map.class );
				}
				catch ( JsonSyntaxException | SQLException e )
				{
					e.printStackTrace();
					sessionCandy = null;
				}
			}
		}
		
		if ( sessionCandy == null )
		{
			int defaultLife = (request.getSite().getYaml() != null) ? request.getSite().getYaml().getInt( "sessions.default-life", 604800 ) : 604800;
			
			if ( candyId == null || candyId.isEmpty() )
				candyId = StringUtil.md5( request.getURI().toString() + System.currentTimeMillis() );
			
			sessionCandy = new Candy( candyName, candyId );
			
			sessionCandy.setMaxAge( defaultLife );
			sessionCandy.setDomain( "." + request.getSite().domain );
			sessionCandy.setPath( "/" );
			
			candies.put( candyName, sessionCandy );
			
			data.put( "ipAddr", request.getRemoteAddr() );
			String dataJson = new Gson().toJson( data );
			
			expires = Common.getEpoch() + defaultLife;
			
			sql.queryUpdate( "INSERT INTO `sessions` (`sessid`, `expires`, `data`)VALUES('" + candyId + "', '" + expires + "', '" + dataJson + "');" );
		}
		
		Loader.getLogger().info( "Session Initalized: " + this );
	}
	
	protected void saveSession()
	{
		SqlConnector sql = Loader.getPersistenceManager().getSql();
		
		data.put( "ipAddr", request.getRemoteAddr() );
		String dataJson = new Gson().toJson( data );
		
		sql.queryUpdate( "UPDATE `sessions` SET `data` = '" + dataJson + "', `expires` = '" + expires + "' WHERE `sessid` = '" + candyId + "';" );
	}
	
	public String toString()
	{
		return candyName + "{id=" + candyId + ",expires=" + expires + ",data=" + data + "}";
	}
	
	/**
	 * Determines if this session belongs to the supplied HttpRequest based on the SessionId cookie.
	 * 
	 * @param request
	 * @return boolean
	 */
	protected boolean matchClient( HttpRequest request )
	{
		Map<String, Candy> requestCandys = pullCandies( request );
		return ( requestCandys.containsKey( candyName ) && getCandy( candyName ).compareTo( requestCandys.get( candyName ) ) );
	}
	
	/**
	 * Returns a cookie if existent in the session.
	 * 
	 * @param key
	 * @return Candy
	 */
	public Candy getCandy( String key )
	{
		return ( candies.containsKey( key ) ) ? candies.get( key ) : new Candy( key, null );
	}
	
	public Map<String, Candy> pullCandies( HttpRequest request )
	{
		Map<String, Candy> candies = new LinkedHashMap<String, Candy>();
		List<String> var1 = request.getHeaders().get( "Cookie" );
		
		if ( var1 == null || var1.isEmpty() )
			return candies;
		
		String[] var2 = var1.get( 0 ).split( "\\;" );
		
		for ( String var3 : var2 )
		{
			String[] var4 = var3.trim().split( "\\=" );
			
			if ( var4.length == 2 )
			{
				candies.put( var4[0], new Candy( var4[0], var4[1] ) );
			}
		}
		
		return candies;
	}
	
	/**
	 * Indicates if this session was previously used in a prior request
	 * 
	 * @return boolean
	 */
	public boolean isStale()
	{
		return stale;
	}
	
	public String getId()
	{
		return candyId;
	}
	
	public void setArgument( String key, String value )
	{
		data.put( key, value );
	}
	
	public String getArgument( String key )
	{
		if ( !data.containsKey( key ) )
			return "";
		
		return data.get( key );
	}
	
	public boolean isSet( String key )
	{
		return data.containsKey( key );
	}
	
	public void setCookieExpiry( int valid )
	{
		sessionCandy.setMaxAge( valid );
	}
	
	// TODO: Fix ME
	public void destroy()
	{
		expires = 0;
		setCookieExpiry( 0 );
	}
	
	public void rearmTimeout()
	{
		// TODO: Extend timeout even longer if a user is logged in.
		
		// Grant the timeout an additional 2 minutes per request
		if ( requestCnt < 6 )
			requestCnt++;
		
		timeout = Common.getEpoch() + defaultTimeout + ( requestCnt * 120000 );
	}
	
	public int getTimeout()
	{
		return timeout;
	}
	
	/**
	 * This method is only to be used to make this session unremovable from memory by the session garbage collector. Be
	 * sure that you rearm the timeout at some point to prevent build ups in memory.
	 */
	public void infiniTimeout()
	{
		timeout = 0;
	}
	
	// TODO: Future add of setDomain, setCookieName, setSecure (http verses https)
	
	public boolean getUserState()
	{
		return ( currentUser != null );
	}
	
	public User getCurrentUser()
	{
		return currentUser;
	}
	
	/**
	 * Logout the current logged in user.
	 */
	public void logoutUser()
	{
		setArgument( "user", null );
		setArgument( "pass", null );
		currentUser = null;
		Loader.getLogger().info( "User Logout" );
	}
	
	public HttpRequest getRequest()
	{
		return request;
	}
	
	public HttpResponse getResponse()
	{
		return request.getResponse();
	}
	
	public Evaling getEvaling()
	{
		if ( eval == null )
			eval = new Evaling( binding );
		
		return eval;
	}
	
	/**
	 * Called when request has finished so that this stale session can nullify unneeded stuff such as the HttpRequest
	 */
	public void releaseResources()
	{
		request = null;
	}
}