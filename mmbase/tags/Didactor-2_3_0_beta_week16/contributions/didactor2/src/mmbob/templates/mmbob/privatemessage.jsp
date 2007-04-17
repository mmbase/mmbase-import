<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
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
<mm:import externid="messageid" />
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
            <tr><td><a href="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid" />"><mm:field name="name" /></a> (<mm:relatednodes type="forumprivatemessage"><mm:last><mm:size /></mm:last></mm:relatednodes>)</td></tr>
        </mm:node>
    </mm:related>
    </mm:node>
    </table>
    </td></tr>
    <tr><td>
    <form action="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid,messageid" />" METHOD="POST">
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th><di:translate key="mmbob.addfolder" /></th></tr>
    <input name="action" type="hidden" value="newfolder">
    <tr><td><input name="newfolder" style="width: 98%" /></td></tr>
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
   <form action="<mm:url page="privatemessageconfirmaction.jsp" referids="forumid,mailboxid,messageid" />" method="post">
   <td valign="top">
    <table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="100%" border="1">
    <tr><th><di:translate key="mmbob.message" /></th><th><di:translate key="mmbob.sender" /></th></tr>
    <mm:present referid="mailboxid">
    <mm:node referid="messageid">
    <tr>
    <td width="50%">
        <br />
        <di:translate key="mmbob.subject" /> : <mm:field name="subject" /><br />
        <di:translate key="mmbob.date" /> : <mm:field name="createtime"><mm:time format="${timeFormat}" /></mm:field><br />
        <di:translate key="mmbob.from" /> : <mm:field name="poster" /> (<mm:field name="fullname" />)<br />
        <di:translate key="mmbob.mailbox" /> : <mm:node referid="mailboxid"><mm:field name="name" /></mm:node><br />
        <br />
    </td>
      <td width="30%">
      <mm:functioncontainer>
                  <mm:field name="poster"><mm:param name="posterid" value="$_" /></mm:field>
          <mm:nodefunction set="mmbob" name="getPosterInfo" referids="forumid">
                        <mm:field name="avatar"><mm:compare value="" inverse="true">
                         <img align="left" width="50" border="0" src="../img.db?<mm:field name="avatar" />" style="margin-top: 5px; margin-right: 10px;">
                        </mm:compare></mm:field>
            <mm:field name="account" /> (<di:person />)<br />
<%-- hh           Level : <mm:field name="level" /> <br /> --%>
            <di:translate key="mmbob.numberofposts" /> : <mm:field name="accountpostcount" /><br />
<%--            Geslacht : <mm:field name="gender" /><br />
            Locatie : <mm:field name="location" /><br /><br /> --%>
                        <di:translate key="mmbob.membersince" /> : <mm:field name="firstlogin"><mm:time format="d/M/yy, HH:mm:ss" /></mm:field><br />
                        <di:translate key="mmbob.lastvisit" /> : <mm:field name="lastseen"><mm:time format="d/M/yy, HH:mm:ss" /></mm:field><br />
          </mm:nodefunction>
      </mm:functioncontainer>
    </td>
    </tr>

    <tr>
        <td colspan="2" height="350" valign="top">
            <h4><mm:field name="subject" /></h4>
            <p>
            <mm:field name="html(body)" />
            </p>
        </td>
    </tr>
    <tr>
        <th colspan="2" height="25" valign="top">
        <di:translate key="mmbob.actions" /> : <input type="submit" name="folderaction" value="delete"> - 
        <input type="submit" name="folderaction" value="forward"> -
        <input type="submit" name="folderaction" value="email"> -
        <input type="submit" name="folderaction" value="move">
        </th>
    </tr>
    </mm:node>
    </mm:present>
    </table>
    </form>
   </td>
 </tr>
</table>
</center>
</html>
</mm:cloud>
