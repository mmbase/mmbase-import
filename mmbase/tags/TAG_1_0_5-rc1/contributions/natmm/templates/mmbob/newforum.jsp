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

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3">Nieuw forum aanmaken</th></tr>

  <form action="<mm:url page="forums.jsp">
                </mm:url>" method="post">
    <tr><th>Naam</th><td colspan="2">
    <input name="name" size="70" value="" style="width: 100%">
    </td></tr>
    <%-- hh <tr><th>Taal</th><td colspan="2">
    <input name="language" size="2" value="en">
    </td></tr> --%>
    <input type="hidden" name="language" value="en">
    <tr><th>Omschrijving</th><td colspan="2">
    <textarea name="description" rows="5" style="width: 100%"></textarea>
    </td></tr>
    <tr><th>Admin account</th><td colspan="2">
    <input name="account" size="70" value="" style="width: 100%">
    </td></tr>
    <tr><th>Admin wachtwoord</th><td colspan="2">
    <input name="password" size="70" value="" style="width: 100%">
    </td></tr>
    <input type="hidden" name="action" value="newforum">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="commit" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="forums.jsp">
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="cancel" />">
    </form>
    </td>
    </tr>

</table>
</mm:cloud>

