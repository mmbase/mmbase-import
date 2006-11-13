Put this into your web.xml. 
With a lot of thanks to Nico.

---André

  <!--
    Filter to create nice urls or paths for human consumption
  -->
  <filter>
    <filter-name>NiceUrls</filter-name>
    <filter-class>nl.toly.mmbase.friendlylink.UrlFilter</filter-class>
	<init-param>
      <param-name>excludes</param-name>
	  <param-value>([.]ico$|[.]jpg$|[.]gif$|[.]png$|[.]css$|[.]js$|[.]jsp$|[.]do$)|/errorpages|/mmbase/|/editors</param-value>
    </init-param>
  </filter>

  <filter-mapping>
    <filter-name>NiceUrls</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
