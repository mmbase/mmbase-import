<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp"
%><%@ include file="/includes/alterheader.jsp" %>

<%@ include file="last_builds.jsp" %>
<div id="relatedcontent"><!-- <p>Related can be put here..</p> --> </div>
<div id="textcontent">
<mm:import externid="page" reset="true">download</mm:import>
<mm:node number="$page">
  <mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
	<mm:node element="articles">
<!-- article -->
    <h2><!-- <mm:field name="number" /> --><mm:field name="title"/></h2>
    <mm:content escaper="links">
      <mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write /></p></mm:isnotempty></mm:field>
    </mm:content>
      <mm:remove referid="news"/><mm:import id="news" /><%@ include file="/includes/urls.jsp" %>
    <mm:content escaper="links">
      <mm:field name="body"><mm:isnotempty><div><mm:write /></div></mm:isnotempty></mm:field>
    </mm:content>
<!-- /article -->
	</mm:node>
  </mm:related>

<h3>Latest MMBase release</h3>
<%-- Last official MMBase release --%>
<mm:list path="pages2,releases,mmevents" searchdir="destination"
  fields="releases.number,mmevents.start"
  orderby="mmevents.start" directions="DOWN" max="1">
  <mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
  <mm:node element="releases">
	<p><strong><mm:field name="name" /> <mm:field name="version" /></strong><br />
	<mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale>
	<mm:field name="intro"><mm:isnotempty><mm:write/><br /></mm:isnotempty></mm:field>
	<mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="UP">
	  <mm:first>Download:<ul></mm:first>
	  <mm:node element="urls">
	    <li><mm:field name="url"><a href="<mm:url page="$_"/>"><mm:field name="description"/></a></mm:field></li>
	  </mm:node>
	  <mm:last></ul></mm:last>
	</mm:related>
  </mm:node>
</mm:list>
<p><a href="<mm:url page="index.jsp" referids="portal">
  <mm:param name="page" value="page_releases" />
</mm:url>">Previous releases &raquo;&raquo;</a></p>

<h3>Packages and other applications</h3>
<h5>Packages</h5>
<p>
  More about the MMBase package manager (apps2) and the packages you can install
  and download with it can be found at <a href="http://www.mmbase.org/packages">this page</a>.
</p>

<h5>Other applications</h5>
<mm:list nodes="page_applications"
  path="pages,posrel,urls"
  fields="posrel.pos">
  <mm:first><ul></mm:first>
    <mm:node element="urls">
	<li><a href="<mm:field name="url" />"><mm:field name="name" /></a>
	<mm:field name="description"><mm:isnotempty><br /><mm:write /></mm:isnotempty></mm:field></li>
	</mm:node>
  <mm:last></ul></mm:last>
</mm:list>

<h3>MMBase builds</h3>
<p>Latest builds from the previous stable branch (MMBase-1_8)</p>
<ul>
<%
Iterator j = getStableBuilds(3).iterator();
while (j.hasNext()) {
  BuildInfo info = (BuildInfo) j.next(); %>
  <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
<% ListIterator l = getReleaseBuilds(3).listIterator();
while (l.hasNext()) l.next();
while (l.hasPrevious()) {
  BuildInfo info = (BuildInfo) l.previous(); %>
  <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<p>Latest builds from the HEAD branch. The 1.8 branch was made on 2006-08-30. Builds after that are 1.9.0.</p>
<ul>
</ul>
<ul>
<% Iterator k = getHeadBuilds(3).iterator();
while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
  <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>
<p><a href="<mm:url page="index.jsp" referids="portal">
  <mm:param name="page" value="page_builds" />
</mm:url>">More builds &raquo;&raquo;</a></p>

<h3>Source</h3>
<p>You can access MMBase's CVS repository anonymously. You must of course have
cvs installed. More about how to access the repository can be found on
<a href="<mm:url page="index.jsp" referids="portal">
  <mm:param name="page" value="page_cvs" />
</mm:url>">these pages</a>.
</p>
</div><!-- /textcontent -->
</mm:node>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
