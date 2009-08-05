<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:node referid="page"><h2><mm:field name="title" /></h2></mm:node>

<%-- news, related to category Developers --%>
<% // First we need to know the size of the total list
int listsize = 0; 
%>
<mm:list nodes="$portal" 
	path="portals,category,posrel,news,mmevents"
	fields="news.title">
	<mm:first><mm:size jspvar="listsizeStr" vartype="String" write="false"><% try { listsize = Integer.parseInt(listsizeStr); } catch(Exception e) {} %></mm:size></mm:first>
</mm:list>
<div class="newsinhoud">
<mm:import externid="o" jspvar="offStr" vartype="String">0</mm:import>
<mm:import jspvar="maxStr" vartype="String">10</mm:import>
<mm:import externid="newsnr" />
<%@ include file="/includes/news-include.jsp" %>
<% 
int offset = 0; 
int max = 0;
try { offset = Integer.parseInt(offStr); } catch(Exception e) {}
try { max = Integer.parseInt(maxStr); } catch(Exception e) {}
%>
<mm:list nodes="$portal" 
	path="portals,category,posrel,news,mmevents"
	fields="news.number,news.title,news.subtitle,news.intro,mmevents.start"
	orderby="mmevents.start" directions="DOWN"
	offset="<%= offStr %>" max="<%= maxStr %>">
	<mm:first>
	<mm:present referid="newsnr"><h4>More news</h4></mm:present>
	<mm:notpresent referid="newsnr"><p>Latest news of interest.</p></mm:notpresent>
	<!-- previous and next -->
	<div align="center">
	<table border="0" cellspacing="0" cellpadding="0" class="newsnextprev">
	<tr>
	  <td align="left">
		<% 
		String str_one = "";
		if (offset > 0) { 
			int this_one = offset - max;
			if (this_one < 0) { this_one = 0; } 
		%>
		<a href="<mm:url referids="portal,page"><mm:param name="o"><%= this_one %></mm:param></mm:url>">&lt;&lt; previous</a>
		<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %>
	  </td>
	  <td><a href="<mm:url referids="portal,page" />">index</a></td>
	  <td><%
		int next = offset + max;
		if (next < listsize && offset >= 0) {
		%>
		  &nbsp;&nbsp;<a href="<mm:url referids="portal,page">"><mm:param name="o"><%= next %></mm:param></mm:url>">next &gt;&gt;</a>
		<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %>
		</td>
	</tr>
	</table>
	</div>
	<!-- /previous and next -->
	</mm:first> <mm:field name="mmevents.start" write="false" id="date" />
	<p><mm:node element="news">
	<mm:relatednodes type="images" max="1"><a href="#" 
	  onClick="javascript:launchCenter('<mm:url page="includes/slideshow.jsp">
	    <mm:param name="image"><mm:field name="number" /></mm:param>
	  </mm:url>', 'center', 550, 740)" title="Click to enlarge image"
	  ><img src="<mm:image template="s(100x100)+part(5,5,42,42)" />" alt="<mm:field name="title" />" width="42" align="right" vspace="2" border="1" /></a></mm:relatednodes>
	<b><mm:write referid="date"><mm:time format=":MEDIUM" /></mm:write> :</b>
	<a href="<mm:url referids="page,portal"><mm:param name="newsnr"><mm:field name="number" /></mm:param><mm:param name="o"><%= offset %></mm:param></mm:url>"><mm:field name="title" /></a>
	<mm:field name="subtitle"><mm:isnotempty><br /><i><mm:write /></i></mm:isnotempty></mm:field>
	<mm:field name="intro"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field>
	</mm:node>
	</p><mm:remove referid="date" />
	<mm:last>
	<!-- previous and next -->
	<div align="center">
	<table border="0" cellspacing="0" cellpadding="0" class="newsnextprev">
	<tr>
	  <td align="left">
		<% 
		if (offset > 0) { 
			int this_one = offset - max;
			if (this_one < 0) { this_one = 0; } 
		%>
		<a href="<mm:url referids="portal,page"><mm:param name="o"><%= this_one %></mm:param></mm:url>">&lt;&lt; previous</a>
		<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %>
	  </td>
	  <td><a href="<mm:url referids="portal,page" />">index</a></td>
	  <td><%
		int next = offset + max;
		if (next < listsize && offset > 0) {
			int next_ones = offset + max;
		%>
		  &nbsp;&nbsp;<a href="<mm:url referids="portal,page">"><mm:param name="o"><%= next %></mm:param></mm:url>">next &gt;&gt;</a>
		<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %>
		</td>
	</tr>
	</table>
	</div>
	<!-- /previous and next -->
	</mm:last>
</mm:list>

</div></div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
