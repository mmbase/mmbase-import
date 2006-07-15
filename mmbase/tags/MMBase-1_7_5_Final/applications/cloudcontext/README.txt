Context security in the cloud. Works only in MMBase >= 1.7.0


How to install.

1) Use this in security.xml:

	<authentication class="org.mmbase.security.implementation.cloudcontext.Authenticate" url="" />
	<authorization	class="org.mmbase.security.implementation.cloudcontext.Verify"	url="" />

2) Install the builders (The builders are not present in the application, install them by
   hand). This means copying the files to the right spot. You might want to use a subdirectory (I
   suggest config/builder/security or so)

3) Install the Security application. It is auto-deploy, so this means copying the files to the right
   spot.

The needed files for step 1) to 3) can be found in the 'config' subdirectory (they correspond to the
same files normally in WEB-INF/config)

4) Restart MMBase.

5) Under 'templates' you find a security administration tool for this implementation. You can put
   it anywhere in your web-app. I suggest under /mmbase/security/.


Michiel Meeuwissen
Publieke Omroep 2003
