<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<%@page import="com.finalist.cmsc.services.community.CommunityServiceMysqlImpl"%>
<jsp:useBean id = "community" 

class="com.finalist.cmsc.services.community.CommunityServiceMysqlImpl" scope="request" 

/>

<%community.setUser(request.getParameter("userText"), 

request.getParameter("passText"), request.getParameter("firstname"), 

request.getParameter("lastname"), request.getParameter("emailadres"));%>

<script type="text/javascript">
<!--
window.location = "admin.jsp"
//-->
</script>