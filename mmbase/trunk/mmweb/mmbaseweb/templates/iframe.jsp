<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:list nodes="$page" path="pages,posrel,urls" max="1">
  <iframe id="miframe" onload="calcIframeHeight();" 
	src="<mm:field name="urls.url"/>" title="<mm:field name="urls.name" />" 
	width="100%" height="90%" frameborder="0"><a href="<mm:field name="urls.url"/>"
	target="_blank"><mm:field name="urls.name" /></a></iframe>
</mm:list>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
