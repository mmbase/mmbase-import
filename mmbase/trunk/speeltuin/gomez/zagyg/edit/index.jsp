<%@page language="java" contentType="text/html;charset=utf-8" import="org.mmbase.security.Rank,java.util.*" errorPage="error.jsp"
%><%@ include file="util/headernocache.jsp"
%><mm:content language="$language" postprocessor="reducespace" expires="0">
<html>
  <head>
    <title>Editors</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
  <mm:cloud loginpage="login.jsp" jspvar="cloud">
  <body>
    <mm:import id="tab">index</mm:import>
    <%@ include file="util/navigation.jsp"%>
    <div id="content">
      <% if (Rank.getRank(cloud.getUser().getRank()).getInt() >= 100) { %>
      <os:cache key="<%="edit_cat_"+cloud.getUser().getIdentifier()%>" time="<%=cacheperiod%>" refresh="<%=needsRefresh%>" scope="application">
        <%@include file="cat.jsp" %>
      </os:cache>
      <% } else { %>
          Access Denied.
      <% }%>
    </div>
  </body>
  </mm:cloud>
</html>
</mm:content>