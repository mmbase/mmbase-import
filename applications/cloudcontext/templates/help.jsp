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
 <h1>HELP</h1>

  <div class="help">
  <p>
   Clarification
  </p>
  <table class="rights">
    <tr><td><input type="checkbox" /></td><td>Disallowed</td></tr>
    <tr><td><input type="checkbox" checked="checked" /></td><td>Allowed</td></tr>
    <tr><td class="parent"><input type="checkbox" /></td><td>Allowed by parent group</td></tr>
    <tr><td class="parent"><input type="checkbox" checked="checked" /></td><td>Allowed by parent group and by this group (if revoked on parent, right remains for this group)</td></tr>
  </table>
  <p>
   'Create' rights are only relevant to nodes of the type 'object type'. If a user has 'create' rights on a certain 'object type' node, then she may create nodes of that type.
  <p>
</div>


 </body>
</mm:cloud>
</html>
</mm:content>