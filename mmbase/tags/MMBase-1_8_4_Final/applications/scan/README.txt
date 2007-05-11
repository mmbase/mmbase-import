SCAN is the legacy templating language of MMBase. You should need this application
only if you are using SCAN already.

How to install:

Copy (or replace) every file from the web-app directory to your web-application. You can do this with a command
like this:

/tmp/scan$ cp -r --reply=yes web-app/*  ~/mmbase/head/build/mmbase/mmbase-webapp/

After a restart of MMBase (which would probably happen automaticly because there is a web.xml in the
above copy action) SCAN should work now. The SCAN-editors can be found in /mmeditors/. A link to the
SCAN examples can be found in /mmexamples/index-scan.jsp

Most SCAN pages only work properly when the application context is "/". If it is not, you should
also edit WEB-INF/web.xml.

