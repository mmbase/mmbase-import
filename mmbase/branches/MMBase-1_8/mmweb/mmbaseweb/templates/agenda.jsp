<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">
<mm:node referid="page"><h2><mm:field name="title" /></h2></mm:node>
<mm:import externid="item" />
<mm:import externid="eventnr"><mm:write referid="item" /></mm:import>
<%@ include file="/includes/agenda_etc.jsp" %>
<div class="newsinhoud">
<%-- news, related to category Developers --%>
<% // First we need to know the size of the total list
int listsize = 0; %><mm:list nodes="$portal" 
	path="portals,category,posrel,event,mmevents"><mm:first><mm:size jspvar="listsizeStr" vartype="String" write="false"><% try { listsize = Integer.parseInt(listsizeStr); } catch(Exception e) {} %></mm:size></mm:first></mm:list> <%-- get the parameter o = offset --%>
<mm:import externid="o" jspvar="offStr" vartype="String">0</mm:import>
<mm:import jspvar="maxStr" vartype="String">10</mm:import><% 
int offset = 0; 
int max = 0;
try { offset = Integer.parseInt(offStr); } catch(Exception e) {}
try { max = Integer.parseInt(maxStr); } catch(Exception e) {}
%>
<mm:list nodes="$portal" 
	path="portals,category,posrel,event,mmevents"
	fields="event.number,event.title,event.subtitle,event.intro,mmevents.start"
	orderby="mmevents.start" directions="DOWN"
	offset="<%= offStr %>" max="<%= maxStr %>">
	<mm:first>
	<mm:present referid="eventnr"><h4>More events</h4></mm:present>
	<mm:notpresent referid="eventnr"><p>Latest events of interest.</p></mm:notpresent>
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
	</mm:first>
	<p><b><mm:field name="mmevents.start"><mm:time format="dd/MM/yyyy" /></mm:field> :</b>
	<mm:node element="event">
	<mm:relatednodes type="images" max="1"><a href="#" onClick="javascript:launchCenter('/includes/slideshow.jsp?image=<mm:field name="number" />', 'center', 550, 740)" title="Click to enlarge image"><img src="<mm:image template="s(200x200)+part(5,5,42,42)" />" alt="<mm:field name="alt" />" width="42" align="right" border="1" /></a></mm:relatednodes >
	<a href="<mm:url referids="page,portal"><mm:param name="eventnr"><mm:field name="number" /></mm:param><mm:param name="o"><%= offset %></mm:param></mm:url>"><mm:field name="title" /></a>
	<mm:field name="subtitle"><mm:isnotempty><br /><i><mm:write /></i></mm:isnotempty></mm:field>
	<mm:field name="intro"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field>
	</mm:node>
	</p>
</mm:list>
	<!-- previous and next -->
	<div align="center" style="margin-top:10pt;">
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
		if (next < listsize && offset >= 0) {
		%>
		  &nbsp;&nbsp;<a href="<mm:url referids="portal,page">"><mm:param name="o"><%= next %></mm:param></mm:url>">next &gt;&gt;</a>
		<% } else { %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<% } %>
		</td>
	</tr>
	</table>
	</div>
	<!-- /previous and next -->
</div>
</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
