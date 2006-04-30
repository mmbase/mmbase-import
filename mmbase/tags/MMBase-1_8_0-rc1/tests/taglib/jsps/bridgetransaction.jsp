<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>

<html>
<body>
<mm:cloud method="http" jspvar="cloud">
<%
	 
	Transaction trans = cloud.getTransaction("my_test_trans");
	trans.commit();

%>
</mm:cloud>
</body>
</html>
