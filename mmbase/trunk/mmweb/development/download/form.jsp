<%-- this JPS uses: 
1. an article (related to the page) to describe the downloadable package/zip or ...
2. 2 or more urls (related to the page) of which:
	a. the first is the link to the download (should be on posrel.pos = 1 !)
	b. the second urls (or more) point to the licenses
	
--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page language="java" contentType="text/html; charset=iso8859-1" %>
<mm:cloud>
<%@ include file="/includes/getids.jsp" %>
<%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<% String[] licenses = request.getParameterValues("license"); %>
<mm:node referid="page">
  <mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
	<mm:node element="articles">
	<%@ include file="/includes/article.jsp" %>
	</mm:node>
  </mm:related>
  
  <mm:import externid="action" />  
  <mm:present referid="action">
	<% int s = 0; %>
	<mm:related path="posrel,urls" searchdir="destination">
	  <mm:first><mm:size jspvar="size" vartype="Integer" write="false"><% s = size.intValue(); %></mm:size></mm:first>
	</mm:related>  
	<%
	if (licenses != null && licenses.length >= (s - 1) ) { 
	%>  
		<mm:related path="posrel,urls"
			fields="posrel.pos,urls.url,urls.name" 
			max="1" offset="0" 
			orderby="posrel.pos">
			<p>Thank you for downloading <strong><mm:field name="urls.name" /></strong>. 
			Your download will start automatically.</p>
			<p>If the download does not start you can use this link:<br />
			<a href="<mm:field name="urls.url" />"><mm:field name="urls.url" /></a></p>
			<iframe src="<mm:field name="urls.url" />" width="1" height="1"></iframe>
		</mm:related>
	<% } else { %>
		<p><strong>You must agree with the license(s)!</strong></p>
		<mm:remove referid="action" />
	<% } %>
  </mm:present>
    
  <mm:notpresent referid="action">
	<form action="<mm:url referids="portal,page"/>" method="post">
	<h3>License</h3>
	<mm:related path="posrel,urls"
	  fields="posrel.pos,urls.url" 
	  max="1" offset="0" 
	  orderby="posrel.pos">
	  Download: <mm:field name="urls.name" /><br />
	</mm:related>
	
	
	<mm:related path="posrel,urls" searchdir="destination"
	  fields="urls.number,urls.name,urls.url" offset="1">
	  <mm:first><p></mm:first>
	  <input type="checkbox" name="license" value="<mm:field name="urls.number" />" /> <mm:field name="urls.name" /> :
	  <a href="<mm:field name="urls.url" />"><mm:field name="urls.url" /></a><br />
	  <mm:last></p></mm:last>
	</mm:related>
	<p><input type="submit" name="action" value="Download" /></p>
	</form>
  </mm:notpresent>
	
</mm:node>

</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
