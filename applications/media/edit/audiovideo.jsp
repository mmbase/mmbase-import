<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:import externid="source" required="true" /><%-- 
   This is used during addition of source. This can popped into the
   'player' frame, to view a particual source.
--%>
<html>
<head>
  <title>Test</title>
</head>
<body>
<mm:locale language="$config.lang">
<mm:cloud>
<mm:write referid="source" />:
<mm:node number="$source" notfound="skip">
  <mm:nodeinfo type="nodemanager" />: <mm:field name="urls()" /> <br />
</mm:node> 
</mm:cloud>
</mm:locale>
</body>
</html>