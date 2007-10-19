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
<mm:import externid="remforum" />

<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <tr><th colspan="3" align="left">Echt forum <mm:node number="$remforum"><mm:field name="name" /></mm:node> verwijderen ?
    <p />
    Dit houd in dat alle gebieden, onderwerpen, reacties en posters worden weg gegooit !!!
  </th></tr>
  </td></tr>
  <tr><td>
  <form action="<mm:url page="forums.jsp"></mm:url>" method="post">
    <p />
    <center>
    <input type="hidden" name="remforum" value="<mm:write referid="remforum" />" /> <input type="hidden" name="action" value="removeforum" />
    <input type="submit" value="<mm:write referid="delete" />">
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

