<!-- START FILE: index.jsp -->
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page language="java" contentType="text/html; charset=utf-8" session="true"
%><%-- WHY is session true! It makes no sense! Or just for this stupid 'Hello <name>'?!! --%>
<%--  NO DOCTYPE, otherwise the menu is not diplayed on the Mac 
--%><mm:content type="text/html">
<mm:cloud>
 <%@include file="login/login.jsp" %>
 <%@include file="includes/getids.jsp" %>
 <mm:node number="$page" notfound="skipbody">
   <mm:relatednodes type="templates">
    <mm:field name="url">
	<mm:include debug="html" page="$_" />
       <mm:import id="templatefound" />
    </mm:field>
   </mm:relatednodes>
   <mm:notpresent referid="templatefound"><%-- show error-page --%>
    <%@include file="/includes/header.jsp"%> 
    <td colspan="2">
    <b><font color="#CC0000">Error:</font></b>
     <p>
       A template should be added to page '<mm:field name="title" />'.
     </p>
    </td>
    <%@include file="/includes/footer.jsp"%>
   </mm:notpresent>
 </mm:node>
</mm:cloud>
</mm:content>
