<%--
  Map this file to jsp
  <servlet-mapping>
    <servlet-name>jsp</servlet-name>
    <url-pattern>*.asf</url-pattern>
  </servlet-mapping>

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="fragment" required="true"  
/><mm:import externid="environment"
>true</mm:import><mm:cloud
><mm:node  number="$fragment"
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
</mm:node></mm:cloud>