<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><mm:content postprocessor="reducespace"><html>
<%@include file="settings.jsp" %>
<head>
    <title>Cloud Context Administration HELPn</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>


<body>
<mm:cloud method="loginpage" loginpage="login.jsp" jspvar="cloud" rank="$rank">
 <%@include file="you.div.jsp" %>
 <mm:import id="current">help</mm:import>
 <%@include file="navigate.div.jsp" %>

  <div class="help">
  <p>
  
  </p>
  <h2>Available rights</h2>
  <table class="rights">
   <mm:import id="operations" vartype="list">create,read,write,delete,change context</mm:import>
    <tr><mm:stringlist referid="operations"><th><mm:write /></th></mm:stringlist></tr>
    <tr>
    <td class="text">'Create' rights are only relevant to nodes of the type 'object type'. If a user has 'create' rights on a certain 'object type' node, then she may create nodes of that type.</td>
    <td class="text">Whether nodes with this context may be read by user/users from group.</td>
    <td class="text">Whether nodes with this context may be changed by user/users from group.</td>
    <td class="text">Whether nodes with this context may be deleted by user/users from group.</td>
    <td class="text">To every node a 'context' string is assocatiated, and to these 'security contexts' rights are attributed. If you have the 'change context' right to a node, then you may change the security context</td>
  </table>

  <h2>Status of a right</h2>
  <table class="rights">
    <tr><td><input type="checkbox" /></td><td class="text">Disallowed, you can grant</td></tr>
    <tr><td><input type="checkbox" checked="checked" /></td><td class="text">Allowed, you can revoke</td></tr>
    <tr><td class="parent"><input type="checkbox" /></td><td class="text">Allowed by parent group, you can allow (here)</td></tr>
    <tr><td class="parent"><input type="checkbox" checked="checked" /></td><td class="text">Allowed by parent group and here (which you can revoke)</td></tr>
    <tr><td></td><td class="text">Disallowed, you may not grant</td></tr>
    <tr><td>X</td><td class="text">Allowed, you may not revoke</td></tr>
    <tr><td class="parent"></td><td class="text">Allowed by parents, you may not grant (here)</td></tr>
    <tr><td class="parent">X</td><td class="text">Allowed by parents and self, you may not revoke (here)</td></tr>
  </table>

</div>
</mm:cloud>

 </body>
</mm:cloud>
</html>
</mm:content>