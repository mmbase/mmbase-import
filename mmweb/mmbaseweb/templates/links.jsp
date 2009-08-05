<%-- template with links, to become the new 'Build with MMBase page'
--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<%@ include file="/includes/relatedpages.jsp" %>
<div id="pagecontent">
<mm:import externid="page" reset="true">page_mmbasewebsites</mm:import>
<mm:list nodes="$page" path="pages,posrel,articles" searchdir="destination"
  orderby="posrel.pos" directions="UP">
  <mm:node element="articles"><%@ include file="/includes/article.jsp" %></mm:node>
</mm:list>
<%-- related urls --%>
<mm:node number="$page">
<mm:relatednodescontainer type="urls" role="posrel">
  <mm:sortorder field="urls.name" direction="UP" />
  <mm:relatednodes>
    <mm:first><ul></mm:first>
	<li><a href="<mm:field name="url" />"><mm:field name="name" /></a>
	<mm:field name="description"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field></li>
    <mm:last></ul></mm:last>
  </mm:relatednodes>
</mm:relatednodescontainer>
</mm:node>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
