<%--
  Map this jsp to /mediafragment* (in web.xml):
  <servlet>
		<servlet-name>ram</servlet-name>
		<description>RAM's</description>
		<jsp-file>/mediaedit/view/mediafragment.jsp</jsp-file>
		<load-on-startup>10</load-on-startup>
	</servlet>

  

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="fragment" required="true"  
/><mm:import externid="format">rm</mm:import><mm:import externid="environment"
>true</mm:import><mm:cloud
><mm:node  number="$fragment"
><mm:field name="format($format)"
><mm:compare value="rm"
><%response.setHeader("Content-Type", "audio/x-pn-realaudio"); 
%><mm:nodeinfo type="nodemanager"
><mm:compare referid="environment" value="true"
><mm:relatednodes type="$_" directions="destination" role="previous"><mm:field name="url(rm)"  />
</mm:relatednodes></mm:compare><mm:field name="url(rm)" />
<mm:compare referid="environment" value="true"
><mm:relatednodes type="$_" directions="source" role="previous"
><mm:field name="url(rm)" />
</mm:relatednodes>
</mm:compare>
</mm:nodeinfo>
</mm:compare><mm:compare value="asf"
><%response.setHeader("Content-Type", "video/x-ms-wmp"); 
%><ASX version="3.0">
 <Title><mm:field name="title" /></Title>
    <mm:nodeinfo type="nodemanager">
  <mm:compare referid="environment" value="true">
    <mm:relatednodes type="$_" directions="destination" role="previous"><%@include file="asxentry.jsp" 
    %></mm:relatednodes>
   </mm:compare>
   <%@include file="asxentry.jsp" %>
  <mm:compare referid="environment" value="true">
<mm:relatednodes type="$_" directions="source" role="previous"
><%@include file="asxentry.jsp" %>
</mm:relatednodes>
</mm:compare>
</mm:nodeinfo>
</ASX>
</mm:compare>

</mm:field>
</mm:node></mm:cloud>