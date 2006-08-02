<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<HTML>
<HEAD>
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

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
   <tr><th>Forum naam</th><th><mm:write referid="numberofmessages" /></th><th><mm:write referid="numberofviews" /></th><th><mm:write referid="numberofmembers" /></th></tr>
  <mm:nodelistfunction set="mmbob" name="getForums">
            <tr>
            <td><a href="index.jsp?forumid=<mm:field name="id" />"><mm:field name="name" /></a></td>
            <td><mm:field name="postcount" /></td>
            <td><mm:field name="viewcount" /></td>
            <td><mm:field name="posterstotal" /></td>
            </tr>
  </mm:nodelistfunction>
</table>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
    <tr><th align="left">Administratie Functies</th></tr>
    <td>
    <p />
    <a href="<mm:url page="newforum.jsp"></mm:url>">Forum toevoegen</a><br />
    <a href="<mm:url page="removeforum.jsp"></mm:url>">Forum verwijderen</a><br />
    <p />
    </td>
    </tr>
    </table>
</mm:cloud>
</center>
</html>
