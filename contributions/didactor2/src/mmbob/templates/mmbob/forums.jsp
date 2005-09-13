<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
   <%

      String bundleMMBob = null;

   %>

   <mm:write referid="lang_code" jspvar="sLangCode" vartype="String" write="false">

      <%

         bundleMMBob = "nl.didactor.component.mmbob.MMBobMessageBundle_" + sLangCode;

      %>

   </mm:write>

<fmt:bundle basename="<%= bundleMMBob %>">
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
   <tr><th><fmt:message key="ForumName"/></th><th><fmt:message key="numberofmessages" /></th><th><fmt:message key="numberofviews" /></th><th><fmt:message key="numberofmembers" /></th></tr>
  <mm:nodelistfunction set="mmbob" name="getForums">
            <tr>
            <td><a href="start.jsp?forumid=<mm:field name="id" />"><mm:field name="name" /></a></td>
            <td><mm:field name="postcount" /></td>
            <td><mm:field name="viewcount" /></td>
            <td><mm:field name="posterstotal" /></td>
            </tr>
  </mm:nodelistfunction>
</table>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
    <tr><th align="left"><fmt:message key="AdminFunctions"/></th></tr>
    <td>
    <p />
    <a href="<mm:url page="newforum.jsp"></mm:url>"><fmt:message key="AddForum"/></a><br />
    <a href="<mm:url page="removeforum.jsp"></mm:url>"><fmt:message key="RemoveForum"/></a><br />
    <p />
    </td>
    </tr>
    </table>
</center>
</html>
</fmt:bundle>
</mm:cloud>
