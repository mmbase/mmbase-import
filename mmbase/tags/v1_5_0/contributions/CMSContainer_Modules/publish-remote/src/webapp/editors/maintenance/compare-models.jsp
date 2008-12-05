<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../publish-remote/globals.jsp"%>
<%@page import="com.finalist.cmsc.maintenance.remotepublishing.Comparer" %>
<%@page import="org.mmbase.remotepublishing.*" %>
<%@page import="java.util.*" %>
<html>
<head>
    <title>Compare models</title>
</head>
    <body>
<mm:cloud jspvar="cloud" rank="administrator" method="http">
<h3>Compare models</h3>
<%
String command = request.getParameter("cmd");
if ("run".equals(command)) {
    String cloud1 = request.getParameter("cloud1");
    String cloud2 = request.getParameter("cloud2");

    if (cloud1 != null && !"".equals(cloud1) && cloud2 != null && !"".equals(cloud2)) {
        Cloud c1 = CloudManager.getCloudWithName(cloud, cloud1);
        Cloud c2 = CloudManager.getCloudWithName(cloud, cloud2);

        List messages = Comparer.compareModels(c1, c2);
        %>
        <p><%
          for (int i = 0; i < messages.size(); i++) {
             String message = (String) messages.get(i);
             if (message.indexOf("ERR") > -1) {
             	%><font color="red"><%= message %></font><br /><%
             }
             else {
             	%><%= message %><br /><%             
             }
          }
        %></p><%
    }
%>
<% } else { %>
    <p>Press 'compare' to compare the clouds.</p>
    <form action="" method="post">
        <input type="hidden" name="cmd" value="run"/>
        <input type="text" name="cloud1" value="staging.server"/>
        <input type="text" name="cloud2" value="live.server"/>
        <input type="submit" value="compare"/>
    </form>
<% } %>
</mm:cloud>
    </body>
</html>
