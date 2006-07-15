<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">

<html>
<head>
<title>MMBase editors (logged on as <%= cloud.getUser().getIdentifier() %>)</title>
</head>
<FRAMESET ROWS="50%,50%" FRAMEBORDER=0 BORDER=0>
    <frame src="unusedlist.jsp" name="unusedlist">
    <frame src="unusedwait.jsp" name="unusededit">
</frameset>
</html>

</mm:cloud>
