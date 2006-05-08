MultilanguageGui is a very basic tool to allow multilanguage support on
a basic mmbase code without changes to the core. Its far from perfect but
can be useful if no other ML system is found. Its used in for example MMBob.

- You need a servlet 2.4 application server like Tomcat 5. 
- You need MMBase 1.8

To install MultiLangaugeGui from bin
--------------------------------------------------------------------------------
  copy WEB-INF/lib/mmbase-multilanguagegui.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mlg in your webdir '/mlg'

  restart your application server

  Once installed you can open the multilanguagegui tool by visiting mlg/index.jsp


To compile MultiLanguageGui and install from sources.
--------------------------------------------------------------------------------
  build and copy build/mmbase-multilanguagegui.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mlg in your webdir '/mlg'

  restart your application server

  Once installed you can open the multilanguage tool by visiting mlg/index.jsp

  For examples howto use it (will add some examples soon) check mmbob templates.

Apr 2006
Daniel Ockeloen
MMCoder, daniel@xs4all.nl
