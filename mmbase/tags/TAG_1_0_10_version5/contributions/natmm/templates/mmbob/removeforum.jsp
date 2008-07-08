<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <form action="<mm:url page="removeforum_confirm.jsp"></mm:url>" method="post">
  <tr><th colspan="3">Selecteer forum dat je wilt verwijderen</th></tr>
  <tr><td colspan="3" align="middle">
    <select name="remforum">
    <mm:nodelistfunction set="mmbob" name="getForums">
        <option value="<mm:field name="id" />"><mm:field name="name" />
    </mm:nodelistfunction>
    </select>
  </td></tr>
  <tr><td>
    <p />
    <center>
    <input type="submit" value="<mm:write referid="delete" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="forums.jsp">
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="cancel" />e">
    </form>
    </td>
    </tr>

</table>
</mm:cloud>

