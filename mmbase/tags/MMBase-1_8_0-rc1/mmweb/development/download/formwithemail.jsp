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
  <mm:import externid="email" />  
  <mm:import externid="name" />  
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
			<p class="message">Thank you for downloading 
			<strong><mm:field name="urls.name" id="urlname" /></strong>.<br />
			Your download will start automatically.</p>
			<p><strong>If the download does not start you can use this link:<br />
			<a href="<mm:field name="urls.url" />"><mm:field name="urls.url" /></a></strong></p>
			<iframe src="<mm:field name="urls.url" />" width="1" height="1"></iframe>
			<mm:present referid="email"><mm:compare referid="email" value="" inverse="true">
			<%@ include file="/includes/secretww.jsp" %>
			<mm:cloud logon="site" password="$ww" method="pagelogon"><mm:remove referid="ww" />
			<mm:createnode type="email">
			  <mm:setfield name="to">jo@mmbase.org</mm:setfield>
			  <mm:setfield name="from"><mm:write referid="email" /></mm:setfield>
			  <mm:setfield name="replyto"><mm:write referid="email" /></mm:setfield>
			  <mm:setfield name="subject">Keep me posted on <mm:write referid="urlname" /></mm:setfield>
<mm:setfield name="body">
The following person downloaded '<mm:write referid="urlname" />'
from the mmbase.org website and likes to be kept informed.

Name:   <mm:write referid="name" />
E-mail: <mm:write referid="email" />

With kind regards,

Your gentle download and e-mail form..

</mm:setfield>
			</mm:createnode>
			</mm:cloud>
			</mm:compare></mm:present>

			
		</mm:related>
	<% } else { %>
		<p class="warning">You must agree with the license(s)!</p>
		<mm:remove referid="action" />
	<% } %>
  </mm:present>
    
  <mm:notpresent referid="action">
	<form action="<mm:url referids="portal,page"/>" method="post">
	<h4>License(s)</h4>
	<mm:related path="posrel,urls"
	  fields="posrel.pos,urls.url" 
	  max="1" offset="0" 
	  orderby="posrel.pos">
	  Download: <mm:field name="urls.name" /><br /><br />
	</mm:related>
	
	
	<mm:related path="posrel,urls" searchdir="destination"
	  fields="urls.number,urls.name,urls.url" offset="1">
	  <mm:first><table border="0" width="480" cellspacing="0" cellpadding="2"></mm:first>
	  <tr>
	    <td align="right"><input type="checkbox" name="license" value="<mm:field name="urls.number" />" /></td>
	    <td>&nbsp;</td>
	    <td><mm:field name="urls.name" /> : <a href="<mm:field name="urls.url" />"><mm:field name="urls.url" /></a></td>
	  </tr>
	  <mm:last></table></mm:last>
	</mm:related>
	<p><strong>Name</strong><br />
	<input type="text" name="name" value="<mm:write referid="name" />" /><br />
	<strong>E-mail addres</strong><br />
	<input type="text" name="email" value="<mm:write referid="email" />" /></p>
	<p><input type="submit" name="action" value="Download" /></p>
	</form>
  </mm:notpresent>
	
</mm:node>

</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
