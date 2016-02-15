﻿# About
**Chiori-chan's Web Server** is a HTTP/TCP Web (Application) Server allowing for both dynamic and static content delivered to both browsers and TCP clients like Android. To provide flexibility, the server includes a powerful Groovy Scripting Engine, along with a full set of tools for exception and bug tracing. Is Groovy not your thing?, well try our extensive Plugin API loosly based on the ever popular CraftBukkit Minecraft Server API, this Plugin API can also extend the Scripting Engine with additional languages such as Lua (WIP). Chiori-chan's Web Server could be considered an Application Server as it gives you the power to create amazing web applications while taking less time and resources, while at the same time utilizing the power of the Java Virtual Machine.

# Jenkins Build Server
Do you like running the latest bleeding edge builds? Well we have the best thing for you since sliced bread. Our Jenkins Build Server, reachable at <http://jenkins.chiorichan.com/job/ChioriWebServer/>. Builds are automatically compiled with each GIT push. Be sure to grab the latest successful build for best results. Also Chiori-chan's Web Server is equipped with an auto updater that runs every 30 minutes by default. We recommend turning it off for production environments. Disclaimer: Be sure to backup your existing installations, We can't be held responsible for damage or unexpected behavior when using bleeding-edge builds.

[![Build Status](http://jenkins.chiorichan.com/buildStatus/icon?job=ChioriWebServer)](http://jenkins.chiorichan.com/job/ChioriWebServer/)
[![Build Status](https://travis-ci.org/ChioriGreene/ChioriWebServer.svg?branch=master)](https://travis-ci.org/ChioriGreene/ChioriWebServer)

# Seeking Help
Hello, my name is Chiori-chan and I'm the sole developer of Chiori-chan's Web Server for well over three years now. And just hitting a little over 62,000 lines of code and 700 commits is a real accomplishment to me, but at the same time the server has started becoming a real beast to deal with and I need help. Sadly, while I dedicate a part-time jobs worth of time to both this project and my web development, I have quite little time to go search for contributors and users alike. And when I do find time, it yields very few results. So I ask that if anyone is interested in contributing or giving my project a trial, please contact me. I have much interest in hearing direct input from users and helpful hints for where I might be going wrong. Thanks much to anyone who takes the time to look over my project, it's become my little baby and I would love to take it to the next step by real world testing it.

# Tutorial Videos
-   <https://www.youtube.com/playlist?list=PL5W-gdSkWP6TOBoL-YDEPZaadwBGXGOyO>

# Official Plugins
### Dropbox Plugin
	Implements the DropBox API into CWS API, allowing scripts to access dropbox files.
	Also serves as a great example on how to use the Maven Dependency Download Feature, see config.yaml.
### Email Plugin
	Implements the Java Mail libraries into the CWS API, allowing scripts to send e-mail like a pro.
### Interactive Console Plugin \[WIP\]
	Reimplements the interactive console feature found in previous versions of CWS.
	Also serves as a great example on how to load Native Libraries without changing the class-path, see config.yaml.
### Lua Plugin
	<https://github.com/ChioriGreene/ChioriWebServer-LuaPlugin>
	Implements Lua as a Scripting Language for Chiori-chan's Web Server
### Templates Plugin
	Implements an easy to use template formatter, also makes exception pages pretty.
### ZXing Plugin
	Implements the barcode XZing libraries into the API, allowing scripts to dynamicly generate barcodes.

# To Do
-   Implement a Server Administration Web Interface.
-   Add better error handling and syntax debugging.
-   Create a Sandbox Mode, i.e., SecurityManager
-   Start a Plugin repository
-   Write Plugin API Documentation. (You can use Bukkit Plugin API JavaDocs for the time being)
-   Better the ChangeLogs and Javadocs.
-   Allow certain events to be thrown on site Groovy files not just Plugins, i.e., webroot/resource/com/chiorichan/events/UserLoginEvent.groovy or NotFoundEvent.groovy
-   Improve built-in file cache system
-   Implement SASS Preprocessor
-   Finish htaccess implementation

# Installation
## Configuration
The server configuration will be located in the file "server.yaml" found within the root folder of the running jar (Will generate with default values on first run) and is in the standard format of YAML.

It is important to note the binding to ports below 1024 on linux (for example port 80, like most web servers run on) will require access to privileged ports. There are many workarounds to this issue but the preferred way is to listen on ports 8080 (http) and 8443 (https) and redirect the traffic using the IPTables firewall.

### About YAML
YAML(tm) is an international collaboration to make a data serialization language which is both human readable and computationally powerful. The founding members of YAML are Ingy döt Net (author of the Perl module <Data::Denter>), Clark Evans, and Oren Ben-Kiki. YAML emerged from the union of two efforts. The first was Ingy döt Net's need for a serialization format for Inline, this resulted in his <Data::Denter> module. The second, was the joint work of Oren Ben-Kiki Clark Evans on simplifying XML within the sml-dev group. YAML was first publicized with a article on 12 May 2001. Oren and Clark's vision for YAML was very similar to Ingy's <Data::Denter>, and vice versa, thus a few days later they teamed up and YAML was born. For more information: [http://www.yaml.org/start.html]

### server.yaml
*Some of these config keys are buggy, deprecated or don't have full implementation yet. I try my best to update this document when I can but some might get overlooked.*

* server.httpHost: null

	The ip address to bind the web server to, null will bind to all.

* server.httpPort: 8080

	The port to start the http web server on.

* server.httpsPort: 0

	The port to start the https web server on. 0 = disabled

* server.tcpHost: null

	The ip address to bind the TCP server to.

* server.tcpPort: 1024

	The port to start the TCP server on.

* server.admin: chiorigreene@gmail.com

	This is the administrator e-mail address for the web server. Future plans to send exceptions and alerts to specified address.

* server.allowDirectoryListing: false

	If the web browser requests a directory and there is no index file should I list the directory contents?

* server.haltOnSevereError: false

	Should the console halt on severe errors which would require you to press 'Ctrl-c' to quit.

* server.enableWebServer: true

	Should the http and https web server start? Useful if you plan on this being strictly a TCP server.

* server.enableTcpServer: false

	Should the TCP server start? Useful is you plan on this being strictly a HTTP/HTTPS web server.

* server.database.database: chiorifw

	Server Database

* server.database.type: mysql

	What driver to use. Currently only supports mysql.

* server.database.host: localhost

	Where to find the database server. Be sure to allow the host if not running locally.

* server.database.port: 3306

	Database port number.

* server.database.username:

	Database Username

* server.database.password:

	Database Password

* settings.permissions-file: permissions.yml
	
	This is the file used to store permissions.

* settings.update-folder: update

	This is the folder used for both server and plugin automatic updates.

* settings.shutdown-message: Server Shutdown

	This is the message sent to devices and users that have active connections

* settings.whitelist: false

	This tells the server if its using a whitelisted user system.

* settings.webroot: webroot

	Defines the directory used to store web page data.

* settings.ping-packet-limit: 100

	Defines the max number of connections per minute. Perfect for DDOS prevention.

* auto-updater.enabled: true

	Is the server allowed to automaticly update ones self. Might want to disable this if you plan to run a custom build.

* auto-updater.on-broken

	Who to warn if the server or plugins detect that they are running broken builds.

* auto-updater.on-update

	Who to tell if an update is ready.

* auto-updater.perferred-channel: stable

	What channel of builds do you prefer. stable, beta, alpha, nightly

* auto-updater.host: dl.chiorichan.com

	What host address do we check and download updates from

* sessions.defaultSessionName: sessionId

	If no session name is set by the site then this is the name of the cookie used to make the session persistent.

* sessions.defaultTimeout: 3600

	Default timeout until the session is destroyed. 3600 = 1 hour

* sessions.defaultTimeoutWithLogin: 86400

	Default timeout until the session is destroyed if a user is present. 86400 = 24 hours

* sessions.defaultTimeoutRememberMe: 604800

	Default timeout until the session is destroyed if a user is present and they selected the remember me (HTTP Argument: remember = true/1). 604800 = 1 week

* sessions.allowNoTimeoutPermission: false

	Allows a logged in entity to have it's session never destroyed using the permission node: chiori.noTimeout. Be sure to manually destroy it if used.

* sessions.rearmTimeoutWithEachRequest: true

	Tells the server to recalculate the sessions timeout with each HTTP request made.

* sessions.maxSessionsPerIP: 6

	Tells the server what is the maximum allowed sessions per IP. If more exist the Persistence Manager will destroy the sessions with the soonest timeout.

* sessions.reuseVacantSessions: true

	Tells the Persistence Manager to reuse sessions that have no secure information (ie. A user login) that match the requesters IP. Great for those pesky requesters that ignore the session cookie.

* sessions.allowIPChange: false

	Sort of a prevention of session hiJacking. If a request has a different IP from the IP stored in the session should it be forced to use a new session?

* accounts.allowLoginTokens: true

	Allow accounts to become persistent using tokens.

* accounts.singleLogin: false

	One login per account.

* accounts.singleLoginMessage: You logged in from another location.

	Kick message when multiple logins are made.

* accounts.debug: false

	Enable account debug.

* accounts.sqlType.enabled: false

	Enable SQL account datastore.

* accounts.sqlType.default: false

	Make SQL datastore the default preferred method, used for creating new accounts.

* accounts.sqlType.table: accounts

	Which table should the SQL datastore use.

* accounts.sqlType.fields: [username, email, phone]

	Which fields can be used to login with.

* accounts.fileType.enabled: true

	Enable File account datastore

* accounts.fileType.default: true

	Make File SQL the default preferred method, used for creating new accounts.

* accounts.fileType.filebase: accounts

	Directory to store File accounts.

* accounts.fileType.fields: [username, email, phone]

	Which fields can be used to login with.

## Packages
Packages are files stored specially for use within multiple pages \(including the Templates Plugin\). Packages are located under the 'resource' directory, found within the site webroot. Directory structure follows a standard namespace order of 'com.example.' or 'io.john.', think of each period as a file seperator, so 'com.example.' would be under 'com/example'. The last part of each package would be the file name without the file extension, the server does the work of selecting the best file based on a list of preferred extensions found in configuration. So 'com.example.widgets.menu' could be translated to file 'com/example/widgets/menu.groovy'. Unforchantly there is currently no way of specifying the extension you are expecting.

Thru the Scripting API, you can include and/or require a package path, e.g., require("com.example.widgets.menu");. Using include\(\) will ignore exceptions thrown, while require\(\) will stop execution in the same event, including FileNotFoundException. Please keep in mind that require and include methods will return the packages as objects, html files to return as a String, while scripts will return the last object used or returned. Output directly to buffer using the standard print\(\) and println\(\) methods.

### Using a Package as an API
Each package script is executed as it's own Java Class, utilizing the ability to return an object, place 'return this' at the end of your script and implement each method you will need.

Package Script Example:
```groovy
def sendEmail( addr )
{
	// Code Here
}

def sayHello()
{
	return "Hello World";
}

return this;
```

Use Case:
```groovy
def api = require( "com.example.api.messaging" );
api.sendEmail( "norm@example.com" );
println api.sayHello();
```

## SSL

Secure Https is disabled by default. To enable either generate a self-signed certificate or obtain an offical one online. Using the ACME Plugin, you can obtain free valid certificates from the Let's Encrypt CA for each of your sites and it's subdomains, additional configuration will be needed.

Each site can have it's own certificate assigned using the configuration options sslCert and sslKey with the site configuration file, each certificate and key file must be in PEM format and located within webroot/[siteId]/ssl directory.

## File Annotations
File Annotations allows you to fine tune the way a file is handled by the server. Annotaions are commonly applied by placing a key and value pair (@key value) within the very first lines of any file, including images. They can also be applied thru SQL routes or thru the Scripting API, e.g., getResponse().setAnnotation(key, value);, and vise-verse reading annotations with, e.g., getResponse().getAnnotation(key);. Keep in mind, not all annotations applied using the Scripting API will be detected as the time they are used by the server has already pasted.

* ssl \(required, ignore, deny\)

	Restricts the server to which protocols it may serve the file over. If say an end user requests a page with the annotaion '@ssl required' over an unsecure connection, i.e., http://, the server will automatically redirect the request to https. If such protocol is not enabled, the server will respond with a FORBIDDEN error. The same is true if '@ssl deny' is set and a request if made over a secure connection but this is only provided as yin and yang and probably should never be used.

### Template Plugin Annotaions
The following are excludely used by the Templates Plugin.

* theme \[package\]

	Sets the theme package to use for this file upon request, page content is placed
at the pagedata marker specified within plugin configuration.

* themeless

	Forces the Templates Plugin to NEVER render this page with a theme.

* noCommons

	Forces the Templates Plugin to not automatically add the common includes to the head tag.

* header \[string\]

	Includes this file after the beginning of the html tag of the page.

* footer \[string\]

	Includes this file at the end of the page before the end of the html tag.

## Run as a Service
It is possible to run Chiori-chan's Web Server as a system service thru many methods that exceed the scope of this readme. Search online for solutions about running a Java instance as a service.

## File Placement
As long as the release jar "server.jar" file is in a directory that is writable, All required subdirectories and files will be created on first run.

## Database
Most features of the server can utilize either file or database based configuration. On first run, the server will create a SQLite database within the server root as a placeholder. You can easily switch to a MySql or H2 database thru configuration. You can use sql file 'frameworkdb.sql' to create a new database.

## Sites
Sites are the equivalent of VirtualHosts on Apache Web Server. To create a new site, create the directory path webroot/[siteId]/config.yaml and place the following content within, of course modifying the contents to your needs:
```yaml
site:
  id: SiteId
  title: Site Title
  domain: example.com
subdomains: []
web:
  allowed-origin: '*'
sessions:
  cookie-name: SessionId
  default-life: 604800
  remember-life: 157680000
scripts:
  login-form: 'signin.html'
  login-post: '/'
database:
  type: none
  host: null
  port: 3306
  database: null
  username: null
  password: null
  prefix: ''
  connectionString: ''
```

## Plugins
Because this web server has a Plugin System loosly based on CraftBukkit you can develop Plugins almost the same way, with the only differences being the API. You can find a nice beginners tutorial for CraftBukkit at <https://forums.bukkit.org/threads/basic-bukkit-plugin-tutorial.1339/>. To install a plugin just place it within the 'plugins' directory located within the server root.

# Version History
For the reasons of preservation, I will keep the version history for the
PHP Framework here but any version 5.2 and up will be the Java Port

Version 1.0
-----------
Original Framework concept first using object oriented programming. Each
page would call framework in the beginning and end of each file which
was later determined to be resource intensive and more work then
desired.

Version 2.0
-----------
First version introducing a loader that would be started using
mod\_rewrite. This version also introduced built-in WebDav support,
Feature later removed due to the issue of maintaining the buggy code.

Version 3
---------
### Subversion 1
First introduction of experimental administration panel. Panel removed
in later versions but there are plans to introduce it.

### Subversion 2
Limited Version, Used as an experimental version to Version 4.0.

Version 4
---------
### Subversion 1 (Betarain)
#### Build 0101
Fourth time completely rewriting source code from scratch. Version 3 or
prior modules not supported in this version.
#### Build 0309
Nothing more then bug fixes.
### Subversion 2 (Betadroid)
#### Build 0319
First introduction release of component based system. Changed some
function names to be more compliment with personal coding standards.
Rewrote the Database Component to use the new PDO instead of the
previous mysql commands. This change allows multiple db connections,
file based db using SQLite and other db types like oracle. Also added a
second level of containers called "Views" which allow for even less
theme code.
#### Build 0326

Many more bug fixes. Finished porting 98% of the user module code to a
component.

#### Build 0606

Finished porting 99% of all outdated modules and code. Fixed many more
bugs.

### Subversion 3 (Sentry)

#### Build 0712

First build safe for lite production use. Also improved local file
loader, Framework will load the index file of a requested folder if it
exists. Some core framework panel code was added to possibly introduce
the framework panel again but later decided to scrap the idea and wait
till later release.

### Subversion 4 (Rainbow Dash)

#### Build 0901

First version to appear on the GitHub. Made some bug fixes.

Version 5
---------

### Subversion 0 (Fluttershy)

#### Build 1106

Again more code rewrites to make the framework more streamlined and
easier to debug.

#### Build 1111

Inported some old code which broke the framework. Fixed in next push.

#### Build 1115/1116

Bug Fixes.

### Subversion 1 (Scootaloo)

#### Build 0106/0107

Introduced Hooks which are like event listeners. Bug Fixes.

### Subversion 2 (Lunar Dream)

#### Build 0825

First attempts to port framework to the Chiori Web Server.

#### Build 0829

Major issues resolved. About 60%-70% of framework ported, 20% discarded
(to be replaced).

Version 6
---------

### Subversion 0 (Sonic Dash)

#### Build 1004

Switched from Resin to Jetty. Removed Quercus and replaced it with our
own Java/PHP hybrid language using the BeanShell libraries

### Subversion 1 (Sonic Dash)

#### Build 1012

Switched from BeanShell to GroovyShell. Now you can write your web
script in uncompiled Java with the joys of Groovy.

### Subversion 2 (Sonic Doom)

#### Build 1212

Some framework instance/memory handling rewrites. Possiblely broken
build.

#### Build 1222

Fixes to the log and console systems. Switched from Jetty to HttpServer
(A builtin class of the JRE). Also implemented the TCP server side of
the Chiori Web Server for use with Android and Standalone Apps.

#### Build 1227

Made structure layout changes to implement a TCP API and better support
future changes.

### Subversion 3 (Flutter Bat)

#### Build 0104

Heavy code rewrites to move much code from the actual Framework into the
new Request, Response and Session classes. All in preperation of a
cleaner and much improved API. Expect this version to be broken until
testing is performed.

#### Build 0105

Rebuilt the way page rendering is handled and implemented Override
Annotations. This wacky creation lets you override or set any varible
like theme, view, title from within any file no matter if it was
redirected by the framework or interpeted directly from file.

#### Build 0106

As of this version, The framework was offical absorbed into the server
as a whole. Scripts made for prior versions will most likely no longer
work.

### Subversion 4 (Rarity Falls)

#### Build 0204

A whole new system for both users and permissions was implemented. If
you want to start using the new permissions system you will need to use
the PermissionsEx plugin.

#### Build 0207

Major improvements to the way sessions are handled. Many session
configurations added.

#### Build 0313

Added a build.xml file so the project can not be built with Apache Ant
1.8. Switch from YAML to Properties for project details/metadata file
(server/src/com/chiorichan/metadata.properties) so Ant/build.xml could
now make versioned binary files.

#### Release 6

-   Changed from using build numbers to release numbers since Jenkins
    implements this.
-   Optimize WebHandler code for improved performance.
-   Added new InputStream consume file util method.
-   Added privilaged port check to NetworkManager.
-   Changed log formating layout. Log messages now show the current
    thread and milliseconds.
-   Changed the shutdown proceedure. System.exit was prematurly
    terminating plugins.
-   Added support for Multipart HTTP Sessions.
-   Added support for applications to embed the Web Server as a library.
    (WIP)
-   Moved Networking Code from Loader to it's own Class.
-   Made the base changes needed to run to application as a Client.
    (WIP)
-   Changed the way GSON loads Maps for Sessions.
-   Added auto updater that works with the Jenkins Build Server at
    <http://jenkins.chiorichan.com>. (WIP)
-   Added ant build.xml for use with the Jenkins Build Server.
-   Added Cartridge Return char support to the ConsoleLogFormatter. (You
    can now make Progress Bars in console. WOOT!)
-   Added InstallationId. Great for installation tracking.
-   Session cleanups and Update Checks are now on a Time Based Rotation.
-   Fixed bug with doubled output in log file.
-   Added option to disable chat colors.
-   Switch metadata file from YAML to Properties.

Version 7 (Pony Feathers)
--------------------------------------

### Subversion 0

#### Release 0

-   Implemented the FileUserAdapter
-   Added a mech to prevent repetitive retrys in
    the SqlConnector.query().
-   Fixed the includes matcher loop bug. We had to reset the matcher
    since the source changes on each loop.
-   Commons can be indefinitely turned off from the config
    option config.noCommons.
-   You can now optionally force the Template Plugin to always render
    page requests. You can disable this with the override @themeless.
-   Made both defaultDocType and defaultTag a configurable option for
    the Template Plugin. See plugins/Template/config.yml
-   Implemented the ability to include packages inside html using " "
-   Bug fix with the way file extension is parsed.
-   Bug fixes to Database Virtual Request Interpreter
-   Bug fixes to doInclude and added non-evaled htm/html.
-   Bug fixes to subdomain load inside Site
-   Added 'framework.sites.autoCreateSubdomains' and
    'framework.sites.subdomainsDefaultToRoot' options in server configs.
-   Moved event system to it's own class, plan to make events usable
    outside of plugins
-   Enhanced FileInterpreter, simplified the PluginManager and
    ServicesManager, Added Groovy Plugin Loader (Still needs reworking
    to actually function.)

### Subversion 1

#### Release 0

-   Fixes to charset of both loading files and output to browser.
    getResponse().setEncoding( "UTF-8" ) implemented.
-   Implemented Embedded Groovy Server Pages. See EmbeddedShell.java
-   SQLAccountAdapter now confirms if additional user field exists in
    table
-   Major code restructuring to hopefully fix many lingering problems.
-   Undid a few of really silly implementations I did.

Version 8 (Pony Toaster)
------------------------

### Subversion 0

#### Release 0

-   Ant build script now creates simple filename jar files.
-   Evaling is now CodeEvalFactory.
-   Simplifying and improvments to EvalPackage and EvalFile methods
    inside WebUtils class.
-   Null and empty check for print method inside of HttpResponse.
-   Added GetLookupAdapter to Accounts and fixes to ScriptingBaseGroovy.
-   Templates plugin now listens for Http Exceptions and responds.
-   Updates to PluginManager, getPluginByName method.
-   Updates to SeaShells and how they handle exceptions.
-   Added Exception and Error events for http requests.
-   Offical release of Version 8.0.0

### Subversion 1

#### Release 0

-   BUG FIX! Rewrites to CodeEvalFactory over a major design flaw
-   BIG FIX! Auto Updater was broken
-   BUG FIX! to Exceptions
-   BUG FIX! Timezone is now forced to UTC when sent to browser
-   BUG FIX! Proper exception is thrown if you forget to close a GSP
    Marker
-   BIG FIX! to common annotations
-   BUG FIX! to Templates Plugin with BaseTemplate.html
-   EXPANSION! to noCommons in the Templates plugin
-   EXPANSION! to date() method
-   EXPANSION! Added additional conditions to the GSP Interpreter
-   NEW FEATURE! CoffeeScripts can now be server side compiled with a
    PreProcessor
-   NEW FEATURE! Implemented File Uploaded a.k.a. Multipart Form
    Requests
-   NEW FEATURE! HTMLCommentParsers
-   NEW FEATURE! PreProcessors, Interpreters and PostProcessors which
    can manipulate requests server-side
-   NEW FEATURE! \[WIP\] Less PreProcessor, currently buggy
-   NEW FEATURE! Image PostProcessor, can resize images using params
-   NEW FEATURE! Implemented the site command which can create, delete
    and view sites
-   NEW METHOD! dateToEpoch(date), allows you to convert M/d/YYYY to
    epoch
-   NEW METHOD! castToLong() method in ObjectUtil
-   NEW METHOD! trim() to StringUtil
-   NEW METHOD! getAllTypes() to ContentTypes class
-   ADDED! url\_to(\[subdomain\]) to HTMLCommentParsers
-   ADDED! include(<package>) to HTMLCommentParsers
-   ADDED! url\_to\_\[login|logout\]() to HTMLCommentParsers
-   ADDED! optional param (altTableClass) to createTable method under
    WebUtils class
-   ADDED! crude cache system with command. Needs much more work
-   ADDED! Mozilla Rhino Library for non-OpenJDK compiles
-   ADDED! LICENSE file
-   ADDED! HEADER file
-   UPDATED! Guava library from v14.0.1 to v17.0.0
-   SWITCHED! to the Gradle Build System from Apache Ant

### Subversion 3

#### Release 0

-   SWITCHED to Netty for HTTP, HTTPS and TCP servers.

Version 9!
----------

### Subversion 0 (a.k.a. Milky Planet)

#### Build 0

-   ADDED! Directory listing feature \[WIP\]
-   ADDED! Ability to load sites from backend, using /fw/\~siteid
-   ADDED! Added CheckStyle plugin to Gradle
-   REWROTE! EventBus
-   REWROTE! PluginManager
-   REWROTE! Permissions System
-   REMOVED! Removed the Console. Will be reimplementing it later as a
    Plugin
-   REMOVED! Old org.json source code
-   UPDATED! Various log improvements
-   EXPANDED! PermissionsEx plugin is no longer needed as it funality is
    built in

#### Build 1

-   UPDATED! Changed default MIME type
-   UPDATED! Encoding UTF-8 used for text MIME, ISO-8859-1 used for all
    others

#### Build 2

-   ADDED! Gradle publish to our Maven Repository
-   ADDED! Several tables are now generated at load
-   FIXED! Finally finished SQLite support
-   UPDATED! Improved FileUploads
-   UPDATED! Major updates to the EvalFactory and associated classes
-   UPDATED! Switch from using byte\[\] to ByteBuf

#### Build 3

-   ADDED! Implemented a basic Query Server and Command System \[WIP\]
-   READDED! Websocket Support \[WIP\]
-   REWROTE! Rewriting of the Template Plugin
-   REWROTE! NetworkManager
-   UPDATED! EvalFactory now returns EvalFactoryResult

### Subversion 1 (a.k.a. Milky Way)

#### Build 0

-   ADDED: Dropbox Plugin
-   ADDED: Server delivers HttpCode 503 to client if server is not fully
    loaded up
-   NEW FEATURE: Added a Plugin Maven Library Downloader. See Dropbox
    plugin config for an example.

#### Build 1

-   ADDED: Route file can now contain comment lines, starting \#
-   CHANGE: Default source directory changed from pages to root of the
    site
-   ADDED: Groovy Script Timeout
-   TWEAK: Missing Rewrite File result
-   TWEAK: ObjectFunc class
-   TWEAK: SessionManager and AutoUpdater

### Subversion 2 (a.k.a. Milky Berry)

#### Build 0

-   REWROTE: Complete overhaul of Accounts and Sessions systems
-   CHANGE: Session Cookie params are not unchangable

#### Build 1

-   REWRITE: Huge overhaul of ErrorReporting, Exceptions and Logging
    subsystems \[WIP\]
-   ADDED: New --install argument
-   CHANGE: Reenabled the Web UI extraction on install and tweaked the
    boot orders
-   REMOVED: ConfigurationManager and HttpUtils finally removed per
    deprecation
-   ADDED: Timing Constraints and Moved methods from CommonFunc to
    Timings class
-   CHANGE: Task Argument Orders
-   CHANGE: Removed "Chiori" from task class names
-   ADDED: Plugins and Dependencies can now load Native Libraries.
    WOOHOO! No more ClassPath crap. \[WIP\]
-   ADDED: Steps taken to further implement automatic ban system \[WIP\]
-   CHANGE: Improvement to HttpError exception
-   CHANGE: Renamed ScheduleManager to TaskManager
-   ADDED: Plaintext passwords now stored in seperate table
-   REWRITE: Plugin initalize subroutine
-   TWEAK: TemplatesPlugin per changes to server base

#### Build 2

-   DEPENDS: Offically updated Netty to 5.0.0.Alpha2 and Groovy to 2.4.3
-   TWEAK: Performance and Coding Standard Improvments per suggested by
    JARchitect

#### Build 3

-   REWROTE: EvalFactory Handling and Processing, Noticable Speed
    Improvment!!!
-   ADDED: Cache, BWFilter, ARGBFilter to PostImageProcessor

#### Build 4

-   ADDED: start.sh created on first runs for unix-like systems,
    debug\_start.sh also created when in development mode
-   FIXED: bug with build.properties not updating when gradle is ran
-   FIXED: FileFunc.directoryHealthCheck() writable bug
-   FIXED: Bug with Web UI not extracting into proper directory
-   FIXED: Bug with Web UI archive not being included in shadowJar,
    technically still not fixed.
-   CHANGE: Improved how permissions hold custom values
-   CHANGE: Improved Plugin Exceptions
-   CHANGE: sendException() now prints html and head tags to output
-   CHANGE: Exception catching improvements for plugins

#### Build 5

-   ADDED: Default permission type, similar to boolean type
-   ADDED/FIXED: PermissionFile backend
-   REMOVED: sendError() method
-   REMOVED: help.yml file
-   CHANGE: to how Permissions save and load
-   CHANGE: Source file license headers
-   CHANGE: PermissionResult.setValue() and setDescription() no longer
    auto-commit changes
-   CHANGE: Moved static methods from Permission to PermissionManager
-   TWEAK: Speed improvements to permission loading, saving, checking
    and logic in general

#### Build 6

-   REWROTE: Massive rewrites to how Permissions and Accounts relate
    with each other
-   REWROTE: Templates Plugin eval subroutines
-   CHANGE: Better implementations of Query Terminal
-   ADDED: Imeplemented AdvancedCommand class
-   ADDED: Virtual entity checks, prevents root and none from being
    saved to backend
-   ADDED: successInit() method to AccountCreator class, used to
    regulate root and none accounts
-   ADDED: New methods within utility classes
-   ADDED: New messaging subsystem system \[WIP\]
-   ADDED: Permission and Group references \[WIP\]
-   CHANGE: Command permission checks
-   CHANGE: Entity maps are now ConcurrentHashMaps
-   CHANGE: sys permission nodes, excluding sys.op, are now true for
    operators
-   CHANGE: Plugin Descriptions now use Yaml over a Map to load
-   CHANGE: Started to seperate Groovy Evaluation code from EvalFactory.
    \[WIP\]
-   CHANGE: Eval is done directly thru EvalContext, removed WebFunc
    eval methods. Excludes include() and require() groovy api
-   FIXED: SyntaxException not properly being caught

#### Build 7

-   CHANGE: Stacktraces now show their true filename verses their script
    name
-   CHANGE: Templates Plugin finds headers relative to theme package,
    i.e., ../../includes/common and ../../includes/<themename>
-   CHANGE: PermissionNamespace moved to utils for uses outside of
    Permissions
-   ADDED: Plugins can bundle libraries within the subdirectory
    "libraries"

#### Build 8

-   FIXED: Found a bug with not checking is WeakReference is unloaded
-   FIXED: Bug Fixes to database logging and prepared statements
-   FIXED: Huge bug with EventBus not creating new EventHandlers for
    each AbstractEvent
-   FIXED: Tasks not canceling on disable
-   FIXED: Token management was buggy
-   FIXED: TemplatesPlugin includes
-   UPDATED: EventBus.callEvent now returns the AbstractEvent for
    stacking
-   UPDATED: EventBus locks using an object instead of self
-   UPDATED: EventHandlers are now tracked from inside EventBus instead
    of each Event
-   UPDATED: Tweaks and improvements to Permission Commands
-   UPDATED: Several tweaks and improvements to permissions subsystem
-   UPDATED: various improvements to EventBus
-   UPDATED: Made updates to Dropbox Plugin
-   UPDATED: Changed maven download URL to JCenter Bintray, Maven
    Central used as a backup location
-   FEATURE: Added H2 Database support, files only
-   FEATURE: \[WIP\] Added datastores, currently only intended to
    replace SQL databases but will also implement file backends.

### Subversion 3 (a.k.a. Milky Polkadot)

#### Build 0
-   FIXED: HTTP log routine was reporting incorrect date and time
-   FIXED: Issues with Account SQL Save
-   UPDATED: Deprecated old unused log arguments and implmented the use
    of a logs directory
-   UPDATED: Changed how Account Subsystem returns results
-   UPDATED: Changed routing log level from FINE to FINER
-   UPDATED: SQLQueryInsert (SQL Datastore) now checks that all required
    columns have been provided before executing query

#### Build 1
-   FIXED: Compatibility issues with SQL Datastores and SQLite
-   UPDATED: Moved task ticks from Timings to new Ticks class for easier
    understanding
-   UPDATED: Refactored much of the startup, shutdown, and restart
    subroutines, streamlined for efficiency
-   UPDATED: AutoUpdater monitors server jar for modification and
    restarts to apply changes. (configurable)
-   FEATURE: \[WIP\] Added new Server File Watcher
-   FEATURE: Implemented optional Watchdog process that monitors a
    seperate spawned JVM instance for crashes and restarts. (use
    --watchdog to enable) Only tested on Linux

# How To Build
You can either build Chiori-chan's Web Server with Eclipse IDE or using Gradle. It should be as simple as executing "./gradlew build" for linux users. Some plugins will also compile but you will have to execute "./gradlew :EmailPlugin:build" to build it. If built with Gradle, you will find the built files inside the "build/dest" directory.

# Coding
Our Gradle enviroment uses the CodeStyle plugin to check coding standards.

* Please attempt at making your code as easily understandable as possible.
* Leave comments whenever possible. Adding Javadoc is even more appreciated when possible.
* No spaces; use tabs. We like our tabs, sorry.
* No trailing whitespace.
* Brackets should always be on a new line.
* No 80 column limit or 'weird' midstatement newlines, try to keep your entire statement on one line.

# Pull Request Conventions
* The number of commits in a pull request should be kept to a minimum (squish them into one most of the time - use common sense!).
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Pull requests should be tested (does it compile? AND does it work?) before submission.
* Any major additions should have documentation ready and provided if applicable (this is usually the case).
* Most pull requests should be accompanied by a corresponding GitHub ticket so we can associate commits with GitHub issues (this is primarily for changelog generation).

# License
Chiori Web Server is licensed under the Mozila Public License Version 2.0. If you decide to use our server or use any of our code (In part or whole), PLEASE, we would love to hear about it, It's not required but it's generally cool to hear what others do with our stuff.

\(C) 2015 Greenetree LLC, Chiori-chan's Web Server.
