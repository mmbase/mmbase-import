About Jircs
===========

Jircs stands for Java IRC Server. It's development started in 2002 for the EO
(http://www.eo.nl) by Jaco de Groot. The Flash clients that communicate with
Jircs are developed by Jelle Katsman.

Because a chat server was needed that could communicate with Flash and MMBase
(http://www.mmbase.org), the content management system used by the EO, but we
didn't want to invent our own chat protocol, we decided to write a chat server
in Java based on the IRC protocol. Two interfaces where created in Jircs to make
it possible to configure Jircs as a normal IRC server and as a chat server
that communicates with flash clients and check with MMBase for existence of
users and channels so people logged in to the website are automatically logged
in to the IRC server.

View jircs.png for a global overview of the internal working of Jircs.

The Repository interface defines the methods for storing and retrieving users
and channels. Jircs contains an IRC and MMBase implementation for the
repository. It is possible to create your own Repository implementation and
configure Jircs to use it.

The translator interface can be implemented to translate the messages that are
passed between the client and the incoming- and outgoing messagepool. Jircs
contains IRC and Flash translators to communicate with normal IRC clients
(using the normal IRC protocol) and Flash clients (using an xml translation of
the IRC protocol).


Requirements
============

- Jakarta commons Digester (automatically downloaded)

- Java 1.4

    MMBase requires Java 1.4. It might be possible to run Jircs (without
    MMBase) on a lower Java version, but this is untested.

- MMBase 1.7

    This release is compiled against MMBase 1.7.0


Install as standalone server without MMBase
===========================================

1. Extract all files from the zip file.

2. Change the attributes operator-password and administrator-password in
   conf/jircs.xml.

3. Execute the following command in the Jircs directory:

   java -classpath <classpath> nl.eo.chat.Server conf/jircs.xml

   Replace <classpath> with all jar files in the lib directory. E.g.
   lib/jircs.jar:lib/<package>.jar:lib/<package>.jar

4. Test your installation using a Flash client connecting to localhost and port
   5557 or an IRC client connecting to localhost and port 6667. View the log
   files in the logs directory if you are having problems.


Install as servlet in a webapp without MMBase
=============================================

1. Extract all files from the zip file.

2. Change the attributes operator-password and administrator-password in
   conf/jircs.xml.

3. Copy jircs.xml into the WEB-INF directory of your webapp.

4. Copy the jircs, nanoxml-lite and jakarta-commons* jar files in the lib direcotry into the
   WEB-INF/lib directory of your webapp.

5. Add the following to the web.xml of your webapp:

   <servlet>
     <servlet-name>jircs</servlet-name>
     <servlet-class>nl.eo.chat.Servlet</servlet-class>
     <init-param>
       <param-name>configfile</param-name>
       <param-value>jircs.xml</param-value>
     </init-param>
     <load-on-startup/>
   </servlet>

6. Start your webapp.

7. Test your installation using a Flash client connecting to localhost and port
   5557 or an IRC client connecting to localhost and port 6667. View the log
   files in the logs directory if you are having problems.


Install as standalone server with MMBase
========================================

1. Extract all files from the zip file.

2. Change the attributes operator-password and administrator-password in
   conf/jircs_mmbase.xml.

3. Extract all the directory mmbase-webapp from the MMBase zip into the
   directory mmbase.

4. Copy the files in mmbase/applications to
   mmbase/mmbase-webapp/WEB-INF/config/applications.

5. Check the MMBase security config to see if the mmbase user, as specified in
   the jircs config file with mmbase-username and mmbase-password, is
   allowed to create nodes.

6. Execute the following command in the Jircs directory:

   java -classpath <classpath> nl.eo.chat.Server conf/jircs_mmbase.xml

   Replace <classpath> with all jar files in the Jircs lib directory and the all
   the files in the MMBase lib directory. E.g.
   lib/<package>.jar:mmbase/mmbase-webapp/WEB-INF/lib/<package>.jar

7. Test your installation using a Flash client connecting to localhost and port
   5557 or an IRC client connecting to localhost and port 6667 and login with
   nick chatexample1 and password chatexample1. View the log files in the logs
   directory if you are having problems.


Install as servlet in a webapp with MMBase
==========================================

1. Extract all files from the zip file.

2. Change the attributes operator-password and administrator-password in
   conf/jircs_mmbase.xml.

3. Copy jircs_mmbase.xml into the WEB-INF directory of your
   MMBase webapp.
   
4. Copy the jircs and nanoxml-lite jar files in the lib direcotry into the
   WEB-INF/lib directory of your MMBase webapp.

5. Add the following to the web.xml of your MMBase webapp:

   <servlet>
     <servlet-name>jircs</servlet-name>
     <servlet-class>nl.eo.chat.Servlet</servlet-class>
     <init-param>
       <param-name>configfile</param-name>
       <param-value>jircs_mmbase.xml</param-value>
     </init-param>
     <load-on-startup/>
   </servlet>

6. Copy the files in mmbase/applications to
   WEB-INF/config/applications of your MMBase webapp.

7. Check the MMBase security config to see if the mmbase user, as specified in
   the jircs config file with mmbase-sername and mmbase-password, is
   allowed to create nodes.

8. Start your webapp.

9. Test your installation using a Flash client connecting to localhost and port
   5557 or an IRC client connecting to localhost and port 6667 and login with
   nick chatexample1 and password chatexample1. View the log files in the logs
   directory if you are having problems.


Make the MMBase install work with ohter MMBase installations
============================================================

1. Make sure all MMBase installations have the builders as found in
   mmbase/applications/Chat/builders

2. Make sure multicast is activated in mmbaseroot.xml and working.


Using the chat engine in combination with MMBase
================================================

This chat package comes with an adjusted MMBase installation of it's own to
simplify the installation process. If the chat engine is started with this
MMBase installation it will also deploy the example chat application to have
some example data. Two example users are installed (chatexample1 and
chatexample2) and related to a groups node. There's also a chatservers node
created. All users related to the groups node are allowed to login to the chat
using their session as password (chatexample1 and chatexample2 for users
chatexample1 and chatexample2).

It is also possible to connect to a remote MMBase instance, that has the MMBase
chat application installed, using RMI. Check if the remote MMBase instance has
RMI active (see modules/rmmci.xml) and specifiy the uri of this remote MMBase
instance in the chat server configuration file.

If you want to create a new chat instance using different nodes do the
following:

- Create a chatservers node.
- Create a groups node and relate all users to this groups node who are allowed
  to login to the chat. The group node can be any type of node, it doesn't need
  to be of type groups. For example a project node with related user can also
  be specified as group node.
- Specify the numbers (or aliases) in the server configfile.
  
If a users node is related to the chatservers node using a rolerel with "o" as
value this user can become operator using the oper command and his username
and password as arguments. Use the MMBase editors to relate users to the
chatservers node.

The moderated mode of a channel can automatically be set by the server using
the workinghours field of the channels node. An example value is 15:00-16:00
which means that the channel will be in moderated mode before 15:00 and after
16:00.

It is also possible to set the working hours at chat server level using the same
format to disallow sending messages to any channels or sending private messages
to any user.

Rolerel relations between users and channels are only created when needed
and removed if possible (if role has an empty value).

Managed by the chat engine:

- Relations between users and channels.
- Mode fields of channels.

Manually managed nodes (using the MMBase editors):

- Relations between users and chatservers.
- The workinghours and blackwordlist fields.


Getting started with IRC
========================

Most IRC clients have a graphical interface that will take care of sending
the prober IRC commands to the server. Usually it is also possible to send
IRC commands by hand. Here are some commands to get you started. More
information about IRC commands can be found on the internet:

List all channels:

  /list

Get operator status:

  /oper <username> <password>

Remove your operator status:

  /mode <nick> -o

Remove a user from a channel:

  /kick <channel> <user>

Ban a user from a channel

  /mode <channel> +b <nick>!*@*

Remove a user ban from a channel

  /mode <channel> -b <nick>!*@*

List all bans on a channel:

  /mode <channel> +b

Schedule a channel to be deleted when last user leaves (mode d -delete channel
when last user leaves- is introduced by this chat engine to be able to keep
channels alive if this mode is not set):

  /mode <channel> +d

Shutdown the server (for administrators only):

  /die


Background information
======================

The chat engine is based on IRC which is specified in RFC 1459, RFC 2810,
RFC 2811, RFC 2812 and RFC 2813.


Future plans
============

- Make it possible to communicate through http on port 80 with webbrowsers
  and/or Flash to prevent problems with firewalls.

