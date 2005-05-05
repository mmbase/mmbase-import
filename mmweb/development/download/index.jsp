<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>

<%@ include file="last_builds.jsp" %>
<div id="relatedcontent">
<mm:list path="pages2,releases,mmevents"
  fields="releases.number,mmevents.start" 
  orderby="mmevents.start" directions="DOWN" max="4">
  <mm:first><h4>Download MMBase</h4><ul></mm:first>
  <mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
  <mm:node element="releases">
    <li><strong><a href="#r<mm:field name="number" />"><mm:field name="name" /> <mm:field name="version"/></a></strong><br />
    <mm:locale language="en"><mm:time time="$releasedate" format="MMMM dd, yyyy"/></mm:locale></li>
  </mm:node>
  <mm:last>
  <li><strong><a href="#stable">Stable branch</a></strong><br />MMBase 1.7</li>
  <li><strong><a href="#HEAD">HEAD branch</a></strong><br />To become 1.8</li>
  </ul></mm:last>
</mm:list>
</div><!-- /div relatedcontent -->
<div id="textcontent">
<mm:node number="$page">
  <mm:related path="posrel,articles" orderby="posrel.pos" directions="UP" searchdir="destination">
    <mm:node element="articles"><%@include file="/includes/article.jsp"%></mm:node>
  </mm:related>
</mm:node>
<%-- LATEST RELEASE --%>
<p>
  All releases and released packages can be found on <a href="<mm:url page="/download/releases" />">/download/releases</a>.
</p>
<mm:list path="pages2,releases,mmevents"
	 fields="releases.number,mmevents.start" 
	 orderby="mmevents.start" directions="DOWN" max="4">
  <mm:import id="releasedate" reset="true"><mm:field name="mmevents.start"/></mm:import>
  <mm:node element="releases">
    <h3><a id="r<mm:field name="number" />"></a><mm:field name="name" /> <mm:field name="version"/></h3>
    <mm:locale language="en"><p><mm:time time="$releasedate" format="MMMM dd, yyyy"/></p></mm:locale>
    <mm:field name="intro"><mm:isnotempty><p class="intro"><mm:write/></p></mm:isnotempty></mm:field>
    <mm:field name="body" escape="p"><mm:isnotempty><p><mm:write /></p></mm:isnotempty></mm:field>
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

<h2><a id="stable"></a>Latest builds from the stable branch (MMBase-1_7)</h2>
<% Iterator j = getStableBuilds(9).iterator() ;%>
<ul>
<% while (j.hasNext()) { 
  BuildInfo info = (BuildInfo) j.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<h2><a id="HEAD"></a>Latest builds from the HEAD branch that is to become the 1.8 release</h2> 
<% Iterator k = getHeadBuilds(9).iterator() ;%>
<ul>
<% while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<%--
<h2>Latest occasional builds</h2> 
<% Iterator i = getOccasionalBuilds(5).iterator() ;%>
<ul>
<% while (i.hasNext()) {
BuildInfo info = (BuildInfo) i.next(); %>
  <li>	<%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a>  </li>
<% } %>
</ul>
--%>
</div><!-- /div textcontent -->
<%@ include file="/includes/alterfooter.jsp" %>

</mm:cloud>
