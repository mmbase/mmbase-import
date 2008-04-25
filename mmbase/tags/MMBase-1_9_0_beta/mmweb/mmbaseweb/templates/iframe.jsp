<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<%@ include file="/includes/relatedpages.jsp" %>
<div id="pagecontent">
<mm:list nodes="$page" path="pages,posrel,articles" searchdir="destination"
  orderby="posrel.pos" directions="UP">
  <mm:node element="articles"><%@ include file="/includes/article.jsp" %></mm:node>
</mm:list>
<mm:list nodes="$page" path="pages,posrel,urls" searchdir="destination" max="1">
  <iframe id="miframe" onload="calcIframeHeight();" 
	src="<mm:field name="urls.url"/>" title="<mm:field name="urls.name" />" 
	width="100%" height="90%" frameborder="0"><a href="<mm:field name="urls.url"/>"
	target="_blank"><mm:field name="urls.name" /></a></iframe><br />
</mm:list>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
