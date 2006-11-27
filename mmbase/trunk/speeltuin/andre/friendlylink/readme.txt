For use upon websites that are structured using MMBase nodes, like for example 
with a nodetype named 'pages'. To use this application in your MMBase instance 
you need to create one or more classes that extend 
org.mmbase.applications.friendlylink.FriendlyLink representing the nodetypes that 
form your sitemap or sitestructure. Look at the source of Pages for an example.

Move the friendlylink directory in mmbase/applications directory to build this 
application using ant. Or change the paths in 'build.xml' accordingly.

Put the following piece of xml to activate the filter into your web.xml. 
With a lot of thanks to Nico.

---André

  <!--
    Filter to create more friendly urls or paths for human consumption.
  -->
  <filter>
    <filter-name>FriendlyLinks</filter-name>
    <filter-class>org.mmbase.applications.friendlylink.UrlFilter</filter-class>
	<init-param>
      <param-name>excludes</param-name>
	  <param-value>([.]ico$|[.]jpg$|[.]gif$|[.]png$|[.]css$|[.]js$|[.]jsp$|[.]do$)|/errorpages|/mmbase/|/editors</param-value>
    </init-param>
  </filter>

  <filter-mapping>
    <filter-name>FriendlyLinks</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
