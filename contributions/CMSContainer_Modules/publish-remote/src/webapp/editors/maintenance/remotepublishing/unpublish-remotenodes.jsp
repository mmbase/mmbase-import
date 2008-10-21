<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<%@page import="com.finalist.cmsc.maintenance.remotepublishing.*"%>
<html>
<head>
    <title>Unpublish Remotenodes</title>
</head>
    <body>
<mm:cloud jspvar="cloud" rank="administrator" method="http">
<H3>Unpublish Remotenodes</H3>
<br />
<%
String command = request.getParameter("cmd");
if ("run".equals(command)) {
    String action = request.getParameter("action");

    if (action != null && !"".equals(action)) {
	%>
        <%= new SqlExecutor().execute(new UnpublishRemotenodes(action)) %>
    <%
    }
%>
<% } else { %>
    <p>Press 'unpublish' to unpublish deleted nodes which were published toremote clouds.</p>
    <form action="" method="post">
        <input type="hidden" name="cmd" value="run"/>
		<input type="submit" name="action" value="view"/>
		<input type="submit" name="action" value="unpublish"/>
    </form>
<% } %>
</mm:cloud>
    </body>
</html>
