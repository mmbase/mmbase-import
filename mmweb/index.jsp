<!-- START FILE: index.jsp -->
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="true"
%>
<%--  NO DOCTYPE, otherwise the menu is not diplayed on the Mac 
--%><mm:cloud
><%@include file="login/login.jsp" 
%><%@include file="includes/getids.jsp" 
%><mm:node number="$page" notfound="skipbody"
><mm:context>
<mm:relatednodes type="templates">
 <!-- <mm:field name="url" /> -->
 <mm:field name="url">
   <mm:log>mm:including <mm:write /></mm:log>
   <mm:include page="$_" />
   <mm:import id="templatefound" />
 </mm:field>
</mm:relatednodes>
<mm:notpresent referid="templatefound" >
 <%@include file="/includes/getids.jsp" %>
 <%@include file="/includes/header.jsp"%>
  <td colspan="2">
  <b><font color="#CC0000">Error:</font></b>
     <br />
     A template should be added to page '<mm:field name="title" />'.
  </td>
  <%@include file="/includes/footer.jsp"%>
</mm:notpresent></mm:context></mm:node></mm:cloud>
<!-- END FILE: index.jsp -->
