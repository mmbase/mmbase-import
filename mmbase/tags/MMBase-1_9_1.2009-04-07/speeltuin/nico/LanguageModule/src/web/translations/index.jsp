<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">

<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
</head>
<FRAMESET COLS="175,*" FRAMEBORDER=0 BORDER=0>
	<frame src="nav.jsp" name="nav">
	<FRAMESET ROWS="90,*" FRAMEBORDER=0 BORDER=0 name="page">
		<frame src="select.jsp" name="search" scrolling=no>
		<frame src="empty.html" name="wizard">
	</frameset>
</frameset>
</html>

</mm:cloud>
