<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language">
<mm:import id="url">create_context.jsp</mm:import>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
<h1><%=getPrompt(m,"create_context")%></h1>
<%@include file="you.div.jsp" %>
<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_context.jsp</mm:param></mm:url>" method="post">
<table>
  <mm:fieldlist type="edit" nodetype="mmbasecontexts">
    <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
    <input type="hidden" name="context" value="new" />
  </mm:fieldlist>
    <tr><td>&nbsp;</td><td><input type="submit" name="create" value="<%=getPrompt(m,"submit")%>" /></td></tr>
</table>
</form>
</mm:cloud>
<a href="<mm:url referids="parameters,$parameters" page="." ><mm:param name="url">index_contexts.jsp</mm:param></mm:url>">Back</a>
</mm:content>