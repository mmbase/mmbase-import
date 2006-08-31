<%@page import="java.util.*"%>
<%@include file="basics.jsp"%>
<%
  request.getSession().invalidate();
	response.sendRedirect("../index.jsp?"+request.getQueryString());
%>
