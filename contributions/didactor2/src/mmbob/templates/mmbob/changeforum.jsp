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

<mm:import externid="forumid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><fmt:message key="ChangeExistingForum"/></th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="start.jsp">
        <mm:param name="forumid" value="$forumid" />
                </mm:url>" method="post">
    <tr><th><fmt:message key="Name"/></th><td colspan="2">
    <input name="name" size="70" value="<mm:field name="name" />" style="width: 100%">
    </td></tr>
    <tr><th><fmt:message key="Language"/></th><td colspan="2">
    <input name="language" size="2" value="<mm:field name="language" />" >
    </td></tr>
    <tr><th><fmt:message key="Description"/></th><td colspan="2">
    <textarea name="description" rows="5" style="width: 100%"><mm:field name="description" /></textarea>
    </td></tr>
        <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="changeforum">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<fmt:message key="commit" />">
    </form>
    </td>
    </mm:node>
    <td>
    <form action="<mm:url page="start.jsp">
        <mm:param name="forumid" value="$forumid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<fmt:message key="cancel" />">
    </form>
    </td>
    </tr>

</table>
</HTML>
</fmt:bundle>
</mm:cloud>

