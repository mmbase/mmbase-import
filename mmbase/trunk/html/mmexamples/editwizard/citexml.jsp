<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<mm:import externid="page" required="true" />
<head>
   <title>EditWizard Examples - XML Shower - <mm:write referid="page" /></title>
</head>
<body>
<pre>
<mm:formatter format="presentxml">
  <mm:include cite="true" page="$page" />
</mm:formatter>
</pre>
</body>
</html>
