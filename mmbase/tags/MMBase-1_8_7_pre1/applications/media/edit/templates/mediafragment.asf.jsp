<%--
  Map this file to jsp
  <servlet-mapping>
    <servlet-name>jsp</servlet-name>
    <url-pattern>*.asf</url-pattern>
  </servlet-mapping>

--%><%@page session="false" %><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><ASX version="3.0"><mm:content type="video/x-ms-wmp">
<mm:import externid="fragment"  />
<mm:present referid="fragment">
<mm:import externid="environment">true</mm:import>
<mm:cloud>
  <mm:node  number="$fragment">
    <Title><mm:field name="title" /></Title>
    <mm:nodeinfo type="nodemanager">
      <mm:compare referid="environment" value="true">
        <mm:relatednodes type="$_" directions="destination" role="previous"><%@include file="asxentry.jsp"%></mm:relatednodes>
      </mm:compare>
      <%@include file="asxentry.jsp" %>
      <mm:compare referid="environment" value="true">
        <mm:relatednodes type="$_" directions="source" role="previous"><%@include file="asxentry.jsp" %></mm:relatednodes>
      </mm:compare>
    </mm:nodeinfo>
  </mm:node>
</mm:cloud>
</mm:present>
</mm:content>
</ASX>