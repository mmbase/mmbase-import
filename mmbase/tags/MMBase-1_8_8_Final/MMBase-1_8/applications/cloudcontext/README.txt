Context security in the cloud. Works only in MMBase >= 1.7.0


How to install.

0) Place mmbase-cloudcontextsecurity.jar in your WEB-INF/lib

1) Use this in security.xml:

	<authentication class="org.mmbase.security.implementation.cloudcontext.Authenticate" url="" />
	<authorization	class="org.mmbase.security.implementation.cloudcontext.Verify"	url="" />

   or:

   remove security.xml (it is present in mmbase-cloudcontextsecurity.jar)

2) The needed builders are present in mmbase-cloudcontextsecurity.jar
   
   If you want to change them, you may take them out and place alternate version in
   config/builders/cloudcontext

3) Restart MMBase. This will auto-deploy an application with some basic users (an 'admin' user with
   password 'admin2k'), groups and contexts.

4) Under 'templates' you find a security administration tool for this implementation. You can put
   it anywhere in your web-app. I suggest under /mmbase/security/.
   Use it to change the admin-password.


Michiel Meeuwissen
Publieke Omroep 2006
