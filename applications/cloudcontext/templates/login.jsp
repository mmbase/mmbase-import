<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="settings.jsp" %><html>
<mm:import id="authenticate">name/password</mm:import>
<head>
  <title><%=getPrompt(m, "Login")%></title>
  <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>
<body class="basic">
  <h2><%=getPrompt(m, "Login")%></h2>
  <mm:cloud method="logout" />

  <%-- login.p.jsp should be on a more general place --%>
  <mm:include attributes="language"  page="/mmbase/edit/basic/login.p.jsp" />

</body>
</html>
