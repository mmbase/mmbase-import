<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@page import="com.finalist.cmsc.maintenance.live.*"%>
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
    String cloud1 = request.getParameter("cloud1");
    String cloud2 = request.getParameter("cloud2");
    String action = request.getParameter("action");

    if (cloud1 != null && !"".equals(cloud1) 
            && cloud2 != null && !"".equals(cloud2)
            && action != null && !"".equals(action)) {
	%>
	
        <%= new SqlExecutor().execute(new UnlinkRemotenodes(cloud1, cloud2, action)) %>
    <%
    }
%>
<% } else { %>
    <p>Press 'unlink' to unlink missing nodes in staging.</p>
    <form action="" method="post">
        <input type="hidden" name="cmd" value="run"/>
        <input type="text" name="cloud1" value="staging.server"/>
        <input type="text" name="cloud2" value="live.server"/>
		<input type="submit" name="action" value="view"/>
		<input type="submit" name="action" value="unlink"/>
    </form>
<% } %>
</mm:cloud>
    </body>
</html>
