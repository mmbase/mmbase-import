<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:import id="url">edit_user.jsp</mm:import>
<mm:cloud  loginpage="login.jsp"  rank="$rank">

<h1><%=getPrompt(m,"create_user")%></h1>

<%@include file="you.div.jsp" %>

<form action="<mm:url referids="parameters,$parameters"><mm:param name="url">commit_user.jsp</mm:param></mm:url>" method="post">
 <table>
   <mm:fieldlist type="edit" nodetype="mmbaseusers">
     <tr><td><mm:fieldinfo type="guiname" /></td><td><mm:fieldinfo type="input" /></td></tr>
   </mm:fieldlist>
    <tr><td><input type="submit"  name="submit" value="<%=getPrompt(m, "submit")%>" /></td></tr>
    <input type="hidden" name="user" value="new" />

   </table>
 </form>
</mm:cloud>
<a href="<mm:url referids="parameters,$parameters" page="." ><mm:param name="url">index_users.jsp</mm:param></mm:url>"><%=getPrompt(m, "back")%></a>

