<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@page import="com.finalist.cmsc.maintenance.staging.*"%>
<html>
<head>
    <title>Unlink Remotenodes</title>
</head>
    <body>
<mm:cloud jspvar="cloud" rank="administrator" method="http">
<H3>Unlink Remotenodes</H3>
<br />
<%
String command = request.getParameter("cmd");
if ("run".equals(command)) {
    String action = request.getParameter("action");

    if (action != null && !"".equals(action)) {
	%>
        <%= new SqlExecutor().execute(new UnlinkRemotenodes(action)) %>
    <%
    }
%>
<% } else { %>
    <p>Press 'unlink' to unlink missing nodes in staging.</p>
    <form action="" method="post">
        <input type="hidden" name="cmd" value="run"/>
		<input type="submit" name="action" value="view"/>
		<input type="submit" name="action" value="unlink"/>
    </form>
<% } %>
</mm:cloud>
    </body>
</html>
