<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
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
  <tr><th colspan="3"><di:translate key="mmbob.editthread" /></th></tr>
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
    <tr><th><di:translate key="mmbob.mood" /></th><td align="middle" colspan="2">
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
    <tr><th><di:translate key="mmbob.type" /></th><td align="middle" colspan="2">
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
    <center><input type="submit" value="<di:translate key="mmbob.commit" />">
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
    <input type="submit" value="<di:translate key="mmbob.cancel" />">
    </form>
    </td>
    </tr>
</table>

</center>
</html>
</mm:cloud>
