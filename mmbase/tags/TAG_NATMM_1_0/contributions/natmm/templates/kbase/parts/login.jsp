<%@page import="java.util.*"%>
<%@include file="basics.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud rank="basic user" method="http" >
<%
    response.sendRedirect("../index.jsp?"+getParamsFormatted(request, "url", request.getParameterMap()));
  %>
  </mm:cloud>

