<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<mm:cloud>
<%@include file="/includes/getids.jsp" %>
<%@include file="/includes/header.jsp" %>

<%@include file="last_builds.jsp" %>
<td class="white" colspan="2" valign="top">

<%-- LAST RELEASE --%>
<h2>latest release</h2>
 <ul>
 <mm:list path="pages2,releases,mmevents" fields="releases.number,mmevents.start" orderby="mmevents.start" directions="DOWN" max="1">
    <li>
    <mm:node element="releases">
	<h3><mm:field name="name"/></h3>
	<mm:field name="version"/>
	<mm:field name="html(intro)"/>                                
        <mm:related path="posrel,urls" orderby="posrel.pos,urls.description" directions="DOWN">
          <mm:first><ul></mm:first>
          <li>
          <mm:node element="urls">
              <a href="<mm:field name="url"/>"><mm:field name="description"/></a>
          </mm:node>
          </li>
	 <mm:last></ul></mm:last>
        </mm:related>
     </mm:node>
</mm:list>
</ul>

<h2>latest occasional builds</h2> 
<% Iterator i = getOccasionalBuilds(5).iterator() ;%>
<ul>
<% while (i.hasNext()) {
 BuildInfo info = (BuildInfo) i.next(); %>
  <li>	<%= info.dateString %> <%= info.remarks %> <a href="<%= info.link %>">view</a>  </li>
<% } %>
</ul>

<h2>latest builds from the stable branch(MMBase-1_6)</h2>
<% Iterator j = getStableBuilds(5).iterator() ;%>
<ul>
<% while (j.hasNext()) { 
  BuildInfo info = (BuildInfo) j.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<%= info.link %>">view</a></li>
<% } %>
</ul>

<h2>latest builds from the (bleeding) HEAD branch</h2> 
<% Iterator k = getHeadBuilds(5).iterator() ;%>
<ul>
<% while (k.hasNext()) {
  BuildInfo info = (BuildInfo) k.next(); %>
 <li><%= info.dateString %> <%= info.remarks %> <a href="<%= info.link %>">view</a></li>
<% } %>
</ul>

</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
