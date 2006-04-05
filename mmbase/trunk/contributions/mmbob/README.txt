This is a forum system somewhat like phpbb but based on mmbase, it can be integrated with a mmbase site. Its a 'create' a new forum (with subareas) by a click of a button. So it allows you to run one mmbob system for multiple sites or one for a whole site. Each forum can have its own settings and look and can for the large part(s) be set using a gui interface.

- needs MMBase.1.8.0 and the contributions multilanguagegui and thememanager.

- to install mmbob follow the steps
  
  build and copy build/mmbase-mmbob.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mmbob in your webdir '/'

  goto the thememanager contribution and build it
  copy build/mmbase-thememanager.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/thememanager to your webdir '/mmbase/'

  goto the multilanguage contribution and build it
  copy build/mmbase-multilanguage.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mlg to your webdir '/mmbase/mlg'
 
  restart your application server

- Once installed you can open the mmbob forum system by visiting mmbob/index.jsp, and read the operational help files from there.

Apr 2006
Daniel Ockeloen
