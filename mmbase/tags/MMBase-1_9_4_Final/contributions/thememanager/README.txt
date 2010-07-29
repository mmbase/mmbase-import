The ThemeManager is a contribution that allows you to 'skin' a webapplication
using css and image files. It comes with edit tools that allow you to assign
themes to different projects and even edit css files from inside your web browser. Its used in many applications in contrib. for example MMBob where it allows you to skin each forum in a different way.

- You need a servlet 2.4 application server like Tomcat 5. 
- You need MMBase 1.8

To install Thememanager from bin
--------------------------------------------------------------------------------
  copy WEB-INF/lib/mmbase-thememanager.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/thememanager in your webdir '/mmbase/thememanager'

  restart your application server

  Once installed you can open the thememanager by visiting /mmbase/thememanager/index.jsp


To compile ThemeManager and install from sources.
--------------------------------------------------------------------------------
  build and copy build/mmbase-thememanager.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/thememanager in your webdir '/mmbase/thememanager'

  restart your application server

  Once installed you can open the thememanager by visiting /mmbase/thememanager/index.jsp

Apr 2006
Daniel Ockeloen
MMCoder, daniel@xs4all.nl
