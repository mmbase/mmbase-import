MMBar is a simple MMBase performance testing tool, its goal is not to provide
a rich system like jmeter to test your site but to allow you to test your MMBase
installation itself. For this reason it has tests like reading from the bridge, writing to the bridge, one or multiple threads. Its very easy to install so also very easy to test for example mysql vs oracle or linux vs windows. Most tests also provide some base numbers so you can compare your results with others. These results give you a idea if your system is performing as expected.

- You need a servlet 2.4 application server like Tomcat 5. 
- You need MMBase 1.8

To install MMBar from bin
--------------------------------------------------------------------------------
  copy WEB-INF/lib/mmbase-mmbar.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mmbar in your webdir '/mmbar'

  restart your application server

  Once installed you can open the mmbar system by visiting mmbar/index.jsp


To compile MMBar and install from sources.
--------------------------------------------------------------------------------
  build and copy build/mmbase-mmbar.jar to your WEB-INF/lib
  install the webpages found in templates/jsp/mmbar in your webdir '/mmbar'

  restart your application server

  Once installed you can open the mmbar system by visiting mmbar/index.jsp

Apr 2006
Daniel Ockeloen
MMCoder, daniel@xs4all.nl
