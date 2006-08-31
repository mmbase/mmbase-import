<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" loginpage="login.jsp" rank="basic user" jspvar="cloud">
<html>
<head>
<title>MMBase editors (ingelogd als <%= "" + cloud.getUser()%>)</title>
</head>
<frameset cols="250,*" frameborder="0" border="0">
	<frame src="nav.jsp" name="nav">
	<frame src="empty.jsp" name="wizard">
</frameset>
</html>
</mm:cloud>
