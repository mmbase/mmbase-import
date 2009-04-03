<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<mm:cloud method="delegate" jspvar="cloud">
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
<mm:import externid="postingid" />

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
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><di:translate key="mmbob.createnewpm" /></th></tr>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,postingid" />" method="post">
    <tr><th><di:translate key="mmbob.to" /></th><td colspan="2">
        <mm:node number="$postingid">
        <mm:field name="poster" />
        <input name="to" type="hidden" value="<mm:field name="poster" />">
        <input name="poster" type="hidden" value="<mm:node referid="posterid"><mm:field name="account" /></mm:node>">
    </td></tr>
    <tr><th><di:translate key="mmbob.subject" /></th><td colspan="2"><input name="subject" style="width: 100%" value="Re: <mm:field name="subject" />"></td></th>
    </mm:node>
    <tr><th><di:translate key="mmbob.message" /></th><td colspan="2"><textarea name="body" rows="20" style="width: 100%"></textarea></td></tr>
    <tr><th>&nbsp;</th><td>
    <input type="hidden" name="action" value="newprivatemessage">
    <center><input type="submit" value="<di:translate key="mmbob.commit" />">
    </form>
    </td>
    <td>
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
