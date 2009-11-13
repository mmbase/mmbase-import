<%@page session="false" language="java" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="nodeid" required="true" />
<html>
 <head>
  <title>NodeHttpReadTest</title>
 </head>
<body>
<mm:node number="$nodeid">
 	<mm:field  name="name" /><br />
    	<mm:field  name="description" /> <br />
</mm:node>
</body>
</html>
</mm:cloud>
