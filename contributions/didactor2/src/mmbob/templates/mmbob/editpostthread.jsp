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
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
  <mm:node referid="postthreadid">
  <tr><th colspan="3"><fmt:message key="EditThread"/></th></tr>
  <form action="<mm:url page="postarea.jsp" referids="forumid,postareaid,postthreadid" />" method="post">
    <tr><th width="200">Status</th><td colspan="2" align="middle">
        <select name="state">
        <mm:field name="state">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>>normal
        <option value="closed" <mm:compare value="closed">selected</mm:compare>>closed
        <option value="pinned" <mm:compare value="pinned">selected</mm:compare>>pinned
        </mm:field>
        </select>
    </td></th>
    <tr><th><fmt:message key="Mood"/></th><td align="middle" colspan="2">
        <select name="mood">
        <mm:field name="mood">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>>normal
        <option value="mad" <mm:compare value="mad">selected</mm:compare>>mad
        <option value="happy" <mm:compare value="happy">selected</mm:compare>>happy
        <option value="sad" <mm:compare value="sad">selected</mm:compare>>sad
        <option value="question" <mm:compare value="question">selected</mm:compare>>question
        <option value="warning" <mm:compare value="warning">selected</mm:compare>>warning
        <option value="joke" <mm:compare value="joke">selected</mm:compare>>joke
        <option value="idea" <mm:compare value="idea">selected</mm:compare>>idea
        <option value="suprised" <mm:compare value="suprised">selected</mm:compare>>suprised
        </mm:field>
        </select>
    <tr><th><fmt:message key="Type"/></th><td align="middle" colspan="2">
        <select name="ttype">
        <mm:field name="ttype">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>>normal
        <option value="note" <mm:compare value="note">selected</mm:compare>>note
        <option value="faq" <mm:compare value="faq">selected</mm:compare>>faq
        <option value="announcement" <mm:compare value="announcement">selected</mm:compare>>announcement
        </select>
        </mm:field>
        </td></th>

    <tr><th>&nbsp;</th><td>
    <input type="hidden" name="action" value="editpostthread">
    <center><input type="submit" value="<fmt:message key="commit" />">
    </td>
    <td>
    </mm:node>
        </form>
    <form action="<mm:url page="postarea.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<fmt:message key="cancel" />">
    </form>
    </td>
    </tr>
</table>

</center>
</html>
</fmt:bundle>
</mm:cloud>
