<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>

<%@include file="last_builds.jsp" %>
<td class="white" colspan="2" valign="top">

<%-- LAST RELEASE --%>
<h2><mm:node number="$page"><mm:field name="title" /></mm:node></h2>
 <mm:list path="pages2,releases,mmevents"
 	fields="releases.number,mmevents.start" 
 	orderby="mmevents.start" directions="DOWN" max="1">
    <mm:node element="releases">
	<h3><mm:field name="name" /> <mm:field name="version"/></h3>
	<mm:field name="html(intro)" />                                
        <mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="DOWN">
        <mm:first><ul></mm:first>
        <li>
		<mm:node element="urls">
		<mm:field name="url">
			<a href="<mm:url page="$_"/>"><mm:field name="description"/></a>
		</mm:field>
		</mm:node>
        </li>
		<mm:last></ul></mm:last>
        </mm:related>
    </mm:node>
</mm:list>

<h2>Latest builds from the stable branch(MMBase-1_7)</h2>
<% Iterator j = getStableBuilds(5).iterator() ;%>
<ul>
<% while (j.hasNext()) { 
  BuildInfo info = (BuildInfo) j.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<h2>Latest builds from the HEAD branch that is to become the 1.8 release</h2> 
<% Iterator k = getHeadBuilds(5).iterator() ;%>
<ul>
<% while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a></li>
<% } %>
</ul>

<h2>Latest occasional builds</h2> 
<% Iterator i = getOccasionalBuilds(5).iterator() ;%>
<ul>
<% while (i.hasNext()) {
 BuildInfo info = (BuildInfo) i.next(); %>
  <li>	<%= info.dateString %> <%= info.remarks %> <a href="<mm:url page="<%= info.link %>" />">view</a>  </li>
<% } %>
</ul>


</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
