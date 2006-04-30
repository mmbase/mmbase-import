<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>

<div id="pagecontent">

<mm:import externid="org" />
<table class="layout">  
<mm:notpresent referid="org">
<tr>
<td><mm:node referid="page">
		<mm:relatednodes type="articles" max="1"
			><%@include file="/includes/article.jsp"
			%><mm:import id="articlefound" 
		/></mm:relatednodes>
		<mm:notpresent referid="articlefound"
			><h2><mm:field name="title" /></h2>
		</mm:notpresent>
	</mm:node>
	<dl><%
	String searchNodes = ""; 
	String searchPath = "organisation";	
	String searchOrder = "organisation.name";	
	%><mm:list nodes="$page" path="pages,posrel,organisation" max="1"><%
		searchNodes = "$page";
		searchPath = "pages,posrel,organisation";
		searchOrder = "posrel.pos";
		%><mm:field name="pages.number" jspvar="pages_number" vartype="String" write="false"
			><% searchNodes = pages_number; 
		%></mm:field
	></mm:list
	><mm:list nodes="<%= searchNodes %>" path="<%= searchPath %>" orderby="<%= searchOrder %>">
		<dt>
			<a href="<mm:url referids="portal,page"
				><mm:param name="org"><mm:field name="organisation.number"/></mm:param
				></mm:url>"><mm:field name="organisation.name" /><br/>
			</a>
		 </dt>
		 <dd></dd>
		 <br/>
	</mm:list>
	</dl>
</td>
</tr>
</mm:notpresent>
<mm:present referid="org">
	<mm:list nodes="$page" path="pages,readmore,pages1" searchdir="destination">
		<mm:import id="personpage"><mm:field name="pages1.number"/></mm:import>
	</mm:list>

	<mm:node referid="org">
<tr>
<td>
	<h2><mm:field name="name" /></h2>
	<mm:field name="description"><mm:isnotempty><p class="intro"><mm:write /></p></mm:isnotempty></mm:field>
	<p>
	<mm:field name="street"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="city"><mm:isnotempty><mm:field name="zipcode" /> <mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="country"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
	</p>
	<p>
	<mm:field name="phone"><mm:isnotempty>T: <mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="homepage"><mm:isnotempty>F: <mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="email"><mm:isnotempty><a href="mailto:<mm:write />"><mm:write /></a></mm:isnotempty></mm:field>
	</p>
	<p><strong>Contact:</strong><br/>
	<mm:field name="contactperson"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="contactphone"><mm:isnotempty><mm:write /><br/></mm:isnotempty></mm:field>
	<mm:field name="contactemail"><mm:isnotempty><a href="mailto:<mm:write />"><mm:write /></a><br/></mm:isnotempty></mm:field>
	</p>
	
	<%@include file="/includes/images.jsp" %>
</td>
<td>
	<%@include file="/includes/backbutton.jsp" %>
	<br/>
	<%@include file="/includes/urls.jsp" %>
	<%@include file="/includes/attachment.jsp" %>
	
	<mm:present referid="personpage">
		  <table class="relationcontainer">
		<mm:related path="posrel,persons" searchdir="source">
		     <mm:first>
		        <tr><th>People</th></tr>
		     </mm:first>
		       <tr>
		        <td>
		          <p>
		          <a href="<mm:url><mm:param name="page"><mm:write referid="personpage"/></mm:param><mm:param name="person"><mm:field name="persons.number"/></mm:param></mm:url>">
		          	<mm:field name="persons.firstname"/> <mm:field name="persons.suffix"><mm:isnotempty><mm:write/></mm:isnotempty></mm:field> <mm:field name="persons.lastname"/>
		          </a>
		          </p>
		        </td>
		       </tr>
		</mm:related>
		  </table>
	</mm:present>
	
</td>
</tr>
	</mm:node>


</mm:present>

</table>
<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
