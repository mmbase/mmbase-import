<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><mm:import externid="language">en</mm:import>
<%@include file="settings.jsp" %><html>
<head>
  <title><%=getPrompt(m, "Login")%></title>
  <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>
<body class="basic">
    <h2><%=getPrompt(m, "Login")%></h2>
     <mm:import externid="logout" />
     <mm:present referid="logout">
      <mm:cloud method="logout" />
     </mm:present>

     <mm:import externid="reason">please</mm:import>
     <mm:import externid="referrer">index.jsp</mm:import>
      <mm:compare referid="reason" value="failed">
        <p class="failed">
          <%=getPrompt(m, "failed")%>
        </p>
      </mm:compare>
      <mm:compare referid="reason" value="rank">
        <p class="failed">
          <%=getPrompt(m, "failed_rank")%>
        </p>
      </mm:compare>

    <table>
      <form method="post" action="<mm:write referid="referrer" />" >
        <tr><td><%=getPrompt(m, "name")%>:</td><td><input type="text" name="username"></td></tr>
        <tr><td><%=getPrompt(m, "password")%>:</td><td><input type="password" name="password"></td></tr>
        <!-- tr><td>Authenticate:</td><td><input type="text" name="authenticate" value="name/password"></td></tr-->
        <tr><td /><td><input type="submit" name="command" value="login"></td></tr>
        <input type="hidden" name="language" value="<mm:write referid="language" />" />
      </form>
    </table>
</body>
</html>
