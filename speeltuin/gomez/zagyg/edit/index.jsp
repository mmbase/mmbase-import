<%@page language="java" contentType="text/html;charset=utf-8" errorPage="error.jsp"
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
      <mm:cloudinfo type="rank">
        <mm:isgreaterthan value="99">
          <os:cache key="<%="edit_cat_"+cloud.getUser().getIdentifier()%>" time="<%=cacheperiod%>" refresh="<%=needsRefresh%>" scope="application">
            <%@include file="cat.jsp" %>
          </os:cache>
        </mm:isgreaterthan>
        <mm:islessthan value="100">
        Access Denied.
        </mm:islessthan>
      </mm:cloudinfo>
    </div>
  </body>
  </mm:cloud>
</html>
</mm:content>