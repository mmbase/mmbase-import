<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@ include file="/includes/getids.jsp" 
%><%@ include file="/includes/alterheader.jsp" %>
<div id="textcontent">

<mm:node number="$page">
  <mm:relatednodes id="docs" type="documentation">
    <h2><mm:field name="title" /></h2>
    <mm:field name="subtitle"><mm:isnotempty><h3><mm:write /></h3></mm:isnotempty></mm:field>
    <mm:field name="html(intro)" escape="none"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field>
	<mm:field name="html(body)" escape="none"><mm:isnotempty><p><mm:write escape="none" /></p></mm:isnotempty></mm:field>
  </mm:relatednodes>
  
  <h2>View CVS</h2>
  <p>The source of MMBase can be browsed 
  <a href="<mm:url page="http://cvs.mmbase.org/viewcvs/" />">on-line</a>.</p>
  <p>The most recent changes can be followed by reading
  <a href="<mm:url page="index.jsp"><mm:param name="page" value="page_cvsmail" /></mm:url>">cvs mail</a>.
  </p>

<%--  <h1>Recent changes</h1>
    <a name="recent">&nbsp;</a>
   <h2>HEAD</h2>
    <a name="head">&nbsp;</a>
  <mm:include cite="true" page="lastchanges.html" />
   <h2>Stable</h2>
    <a name="stable">&nbsp;</a>
  <mm:include cite="true" page="lastchanges-stable.html" />  --%>
</mm:node>

</div>
<%@ include file="/includes/alterfooter.jsp" %>
</mm:cloud>
