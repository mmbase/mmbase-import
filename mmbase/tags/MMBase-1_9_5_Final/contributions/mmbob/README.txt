This is a forum system somewhat like phpbb but based on mmbase, it can be integrated with a mmbase site. You can create a new forum with subareas by a click of a button. So it allows you to run one mmbob system for multiple sites or one for a whole site. Each forum can have its own settings and look and can for the large part(s) be set using a gui interface.

- You need a servlet 2.4 application server like Tomcat 5. 
- You need MMBase 1.8.1 (or higher) or MMBase-1.9
  NOTE: For the moment we want mmbob to compile against both MMBase 1.8 and MMBase-1.9 without having
        to revert to branches. So HEAD must compile also agains MMBase 1.8.

- You need contribution multilanguagegui
- You need contribution thememanager

To install MMBob from bin
--------------------------------------------------------------------------------
  copy WEB-INF/lib/mmbase-mmbob.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mmbob in your webdir '/mmbob'

  download the thememanager.zip
  copy WEB-INF/mmbase-thememanager.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/thememanager to your webdir '/mmbase/thememanager'

  download the multilanguagegui.zip
  copy WEB-INF/mmbase-multilangaugegui.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mlg to your webdir '/mmbase/mlg'

  restart your application server

  Once installed you can open the mmbob forum system by visiting mmbob/index.jsp, and read the operational help files from there.


To compile MMBob and install from sources.
--------------------------------------------------------------------------------
  build and copy build/mmbase-mmbob.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mmbob in your webdir '/mmbob'

  goto the thememanager contribution and build it
  copy build/mmbase-thememanager.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/thememanager to your webdir '/mmbase/thememanager'

  goto the multilanguage contribution and build it
  copy build/mmbase-multilanguage.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mlg to your webdir '/mmbase/mlg'

  restart your application server

  Once installed you can open the mmbob forum system by visiting mmbob/index.jsp, and read the operational help files from there.

Apr 2006
Daniel Ockeloen
MMCoder, daniel@xs4all.nl
