<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
  <h2>Documentation</h2>
  <p>
  <mm:list nodes="$page" path="pages1,posrel,pages2,urls" 
    orderby="posrel.pos" directions="UP" searchdir="destination">
    <a href="<mm:field name="urls.url"/>" target="docs"><mm:field name="urls.name"/></a><br />
  </mm:list>
  </p>
  <h2>ApiDocs</h2>
  <p>
  <mm:list nodes="$page" path="pages1,pages2,posrel,pages3,urls" 
    orderby="posrel.pos" directions="UP" searchdir="destination">
    <a href="<mm:field name="urls.url"/>" target="docs"><mm:field name="urls.name"/></a><br />
  </mm:list>
  </p>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
