<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="source" required="true" />
<mm:import id="language">nl</mm:import>
<%-- 
   This is used during addition of source. This can popped into the
   'player' frame, to view a particual source.
   TODO: determin right format and display it.
--%>
<html>
<head>
  <title>Test</title>
</head>
<body>
<mm:locale language="$language">
<mm:cloud>

<mm:node number="$source" notfound="skip">
  <mm:field name="url" /> <br />
 </mm:node> 

</mm:cloud>
  </mm:locale>
  </body>
</html>