<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%-- @ taglib uri="http://www.opensymphony.com/oscache" prefix="os"
--%><%@page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud><%@include file="/includes/getids.jsp"
%><%@include file="/includes/header.jsp"%>

<!-- os:cache key="repository" cron="30 4 * * *"-->
 <mm:node number="$page">
 <td colspan="2">
  <ul>
   <mm:relatednodes id="docs" type="documentation">
     <li><a href="#<mm:field name="number" />"><mm:field name="title" /></a></li>
   </mm:relatednodes>
     <li><a href="#viewcvs">View CVS</a></li>
     <li><a href="#viewcvs">Recent changes</a></li
      <ul>
     <li><a href="#head">HEAD</a></li>
     <li><a href="#head">Stable</a></li>
      </ul>
    </li>
  </ul>
  <mm:relatednodes referid="docs">
    <a name="<mm:field name="number" />">&nbsp;</a>
    <h1><mm:field name="title" /></h1>
    <h2><mm:field name="subtitle" /></h2>
    <mm:field name="html(intro)" />
    <mm:field name="html(body)" />
  </mm:relatednodes>
  
  <h1>View CVS</h1>
    <a name="viewcvs">&nbsp;</a>
  <p>
   The source of MMBase can be browsed
   <a href="<mm:url page="/viewcvs/" />">on-line</a>
  </p>
  <h1>Recent changes</h1>
    <a name="recent">&nbsp;</a>
   <h2>HEAD</h2>
    <a name="head">&nbsp;</a>
  <mm:include cite="true" page="lastchanges.html" />
   <h2>Stable</h2>
    <a name="stable">&nbsp;</a>
  <mm:include cite="true" page="lastchanges-stable.html" />
 </td>
  </mm:node>
<!-- /os:cache -->

 <%@include file="/includes/footer.jsp"%>
</mm:cloud>


