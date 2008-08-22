<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<html>
<head>
   <title><di:translate key="mmbob.mmbaseforum" /></title>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="boxname"><di:translate key="mmbob.inbox" /></mm:import>
<mm:import externid="mailboxid" />
<mm:import externid="pathtype">privatemessages</mm:import>
<mm:import externid="posterid" id="profileid" />

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
<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" style="margin-top : 20px;" width="95%">
 <tr>
   <td width="160" valign="top">
    <table cellpadding="0" width="150">
    <tr><td>
    <table cellpadding="0" class="list" cellspacing="0" width="150">
    <tr><th><di:translate key="mmbob.folder" /></th></tr>
    <mm:node referid="posterid">
    <mm:related path="posrel,forummessagebox">
        <mm:node element="forummessagebox">
            <mm:field name="name">
            <mm:notpresent referid="mailboxid">
            <mm:compare referid2="boxname">
                <mm:remove referid="mailboxid" />
                <mm:import id="mailboxid"><mm:field name="number" /></mm:import>
            </mm:compare> 
            </mm:notpresent> 
            </mm:field>
            <tr><td><a href="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid"><mm:param name="mailboxid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="name" /></a> (<mm:relatednodes type="forumprivatemessage"><mm:last><mm:size /></mm:last></mm:relatednodes>)</td></tr>
        </mm:node>
    </mm:related>
    </mm:node>
    </table>
    </td></tr>
    <tr><td>
        <form action="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid" />" METHOD="POST">
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th><di:translate key="mmbob.addfolder" /></th></tr>
    <tr><td><input name="newfolder" style="width: 98%" /></td></tr>
        <input name="action" type="hidden" value="newfolder">
    </table>
    </form>
    </td></tr>
    <tr><td>
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th colspan="3"><di:translate key="mmbob.pmquota" /></th></tr>
    <tr><td colspan="3"><di:translate key="mmbob.youusing" /></td></tr>
    <tr><td colspan="3"><img src="images/green.gif" height="7" width="20"></td></tr>
    <tr><td align="left" width="33%">0%</td><td align="middle" width="34%">50%</td><td align="right" width="33%">100%</td></tr>
    </table>
    </td></tr>
    </table>
   </td>
   <td valign="top">
    <table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="100%" border="1">
    <tr><th><di:translate key="mmbob.subject" /></th><th><di:translate key="mmbob.sender" /></th><th><di:translate key="mmbob.date" /></th><th></th></tr>
    <mm:present referid="mailboxid">
    <form action="<mm:url page="privatemessagesconfirmaction.jsp" referids="forumid,mailboxid" />" method="post">
    <mm:node referid="mailboxid">
    <mm:relatednodes type="forumprivatemessage">
    <mm:first>
        <mm:import id="messagesfound">true</mm:import>
    </mm:first>
    <tr><td width="50%"><a href="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid"><mm:param name="messageid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="subject" /></a></td><td width="25%" ><mm:field name="poster" /> (<mm:field name="fullname" />)</td><td width="25%"><mm:field name="createtime"><mm:time format="${timeFormat}" /></mm:field></td><td><input type="checkbox" name="selectedmessages"></td></tr>
    </mm:relatednodes>
    </mm:node>
    <tr>
        <th colspan="4">
        <di:translate key="mmbob.actions" /> : <input type="submit" name="folderaction" value="new"> -
        <mm:present referid="messagesfound">
        <input type="submit" name="folderaction" value="delete"> - 
        <input type="submit" name="folderaction" value="forward"> -
        <input type="submit" name="folderaction" value="email"> -
        <input type="submit" name="folderaction" value="move">
        </mm:present>
        <mm:notpresent referid="messagesfound">
            <input type="submit" name="folderaction" value="delete mailbox">
        </mm:notpresent>
        </th>
    </tr>
    </form>
    </mm:present>
    </table>
   </td>
 </tr>
</table>
</center>
</html>
</mm:cloud>
