DESCRIPTION:

  * Community Module

CONFIGURATION:

  * Locatie spring-community.xml (in web.xml) en spring-community.properties (in spring-community.xml)
  
  * web.xml
  
	  <context-param>
	    <param-name>contextConfigLocation</param-name>
		  <param-value>WEB-INF/spring-community.xml</param-value>
		</context-param>
		
		<listener>
		  <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
		</listener>
	
		<filter>
			<filter-name>SecurityFilterChainProxy</filter-name>
			<filter-class>org.acegisecurity.util.FilterToBeanProxy</filter-class>
			<init-param>
			  <param-name>targetClass</param-name>
			  <param-value>org.acegisecurity.util.FilterChainProxy</param-value>
			</init-param>
	  </filter>
	  
	  <filter-mapping>
	    <filter-name>SecurityFilterChainProxy</filter-name>
	    <url-pattern>/*</url-pattern>
	  </filter-mapping>
   
  
  * modulesmenu.jsp

		<mm:haspage page="/editors/community/index.jsp">
		  <li class="versioning">
		    <a href="<mm:url page="../community/index.jsp"/>" target="rightpane"><fmt:message key="modules.community" /></a>
		  </li>
		</mm:haspage> 

TODO:

  * Maintenance (Struts) of Authentication, Authorities and Permissions
  * Login / Logout portlet
  * Dependencies (all the libraries for Spring, Hibernate)
  * Authorization (Permissions on Resources)