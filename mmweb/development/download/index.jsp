<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">

<%@ include file="last_builds.jsp" %>

<mm:node number="$page">
  <mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
	<mm:node element="articles"><%@include file="/includes/article.jsp"%></mm:node>
  </mm:related>
</mm:node>

<div id="textcontent">
<%-- LAST RELEASE --%>
 <mm:list path="pages2,releases,mmevents"
 	fields="releases.number,mmevents.start" 
 	orderby="mmevents.start" directions="DOWN" max="1">
	<mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
    <mm:node element="releases">
		<h3><mm:field name="name" /> <mm:field name="version"/></h3>
		<mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale>
		<mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write/></p></mm:isnotempty></mm:field>
		<mm:field name="body" escape="p"><mm:isnotempty><mm:write/></mm:isnotempty></mm:field>
        <mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="DOWN">
        <mm:first><ul></mm:first>
        <li>
		<mm:node element="urls">
		<mm:field name="url"><a href="<mm:url page="$_"/>"><mm:field name="description"/></a></mm:field>
		</mm:node>
        </li>
		<mm:last></ul></mm:last>
        </mm:related>
    </mm:node>
</mm:list>

<h2>Latest builds from the stable branch (MMBase-1_7)</h2>
<% Iterator j = getStableBuilds(9).iterator() ;%>
<ul>
<% while (j.hasNext()) { 
  BuildInfo info = (BuildInfo) j.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<h2>Latest builds from the HEAD branch that is to become the 1.8 release</h2> 
<% Iterator k = getHeadBuilds(9).iterator() ;%>
<ul>
<% while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<h2>Occasional builds</h2> 
<% Iterator i = getOccasionalBuilds(5).iterator() ;%>
<ul>
<% while (i.hasNext()) {
 BuildInfo info = (BuildInfo) i.next(); %>
  <li>	<%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a>  </li>
<% } %>
</ul>


</div></div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
