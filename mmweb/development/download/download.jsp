<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="pagecontent">

<%@ include file="last_builds.jsp" %>

<mm:node number="$page">
  <h2><mm:field name="title" /></h2>
  <mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
	<mm:node element="articles"><%@ include file="/includes/article.jsp" %></mm:node>
  </mm:related>
</mm:node>

<h3>Latest MMBase releases</h3>
<%-- Last official MMBase release --%>
<mm:list path="pages2,releases,mmevents" searchdir="destination"
  fields="releases.number,mmevents.start" 
  orderby="mmevents.start" directions="DOWN" max="1">
  <mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
  <mm:node element="releases">
	<p><strong><mm:field name="name" /> <mm:field name="version" /></strong><br />
	<mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale>
	<mm:field name="intro"><mm:isnotempty><mm:write/><br /></mm:isnotempty></mm:field>
	<mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="DOWN">
	  <mm:first>Download:<br /></mm:first>
	  <mm:node element="urls">
	    <mm:field name="url"><a href="<mm:url page="$_"/>"><mm:field name="description"/></a><br /></mm:field>
	  </mm:node>
	</mm:related></p>
  </mm:node>
</mm:list>
<p><a href="#">Other releases &raquo;&raquo;</a></p>


<h3>Latest packages</h3>

<h3>Latest MMBase builds</h3>
<p>Latest builds from the stable branch (MMBase-1_7)<br />
<% 
Iterator j = getStableBuilds(3).iterator();
while (j.hasNext()) { 
  BuildInfo info = (BuildInfo) j.next(); %>
 <%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a><br />
<% } %>
</p>

<p>Latest builds from the HEAD branch that is to become the 1.8 release<br />
<% Iterator k = getHeadBuilds(3).iterator();
while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
  <%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a><br />
<% } %>
</p>

<h3>Source</h3>
<p>You can access MMBase's CVS repository anonymously. You must of course have
cvs installed. More about how to access the repository can be found on 
<a href="<mm:url page="index.jsp" referids="portal"><mm:param name="page" value="page_cvs" /></mm:url>">these pages</a>.
</p>

</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
