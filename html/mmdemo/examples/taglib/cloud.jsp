<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<body>
<%@ include file="menu.jsp"%>
<!-- some examples: -->
<%-- <mm:cloud method="anonymous"> --%>
<%-- <mm:cloud method="http"> --%>
<%-- <mm:cloud logon="foo" method="http"> --%>
<%-- <mm:cloud logon="foo" pwd="bar"> --%>
<%-- <mm:cloud method="http" rank="basic user"> --%>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
logged on as: <%= cloud.getUser().getIdentifier() %><br />
</mm:cloud>
<a href="logout.jsp">logout</a>
</body>
</html>
