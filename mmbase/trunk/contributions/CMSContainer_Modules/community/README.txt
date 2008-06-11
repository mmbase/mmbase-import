DESCRIPTION:

* Community Module
Adds core functionality for User credentials, Personal information, Groups, Roles and User Preferences.  

CONFIGURATION:
  * add extra community database configuration jdbc.

  * Location spring-community.xml (in web.xml) and spring-community.properties (in spring-community.xml)
  
  * web.xml changes, see this file for an example:
  /CMSContainer_Demo*/demo.cmscontainer.org/war-community/src/webapp/WEB-INF/web.xml
  
  Example:
  
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
   
 
TODO:
  * Maintenance (Struts) of Authentication, Authorities and Permissions
  * Login / Logout portlet or even better: implement this using the servlet filter
  * Authorization (Permissions on Resources)