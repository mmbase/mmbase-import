<%@page import="java.util.*"%>
<%@include file="basics.jsp"%>
<%
  request.getSession().invalidate();
  response.sendRedirect("../index.jsp?"+getParamsFormatted(request, "url", request.getParameterMap()));
%>
