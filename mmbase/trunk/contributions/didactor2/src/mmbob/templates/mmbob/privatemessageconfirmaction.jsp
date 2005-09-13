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
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" />
   <title><fmt:message key="MMBaseForum"/></title>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="boxname"><fmt:message key="Inbox"/></mm:import>
<mm:import externid="mailboxid" />
<mm:import externid="messageid" />
<mm:import externid="folderaction" />
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
    <tr><th><fmt:message key="Folder"/></th></tr>
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
    <form action="" METHOD="POST">
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th><fmt:message key="AddFolder"/></th></tr>
    <tr><td><input name="newfolder" style="width: 98%" /></td></tr>
    </table>
    </form>
    </td></tr>
    <tr><td>
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th colspan="3"><fmt:message key="PMQuota"/></th></tr>
    <tr><td colspan="3"><fmt:message key="YouUsing"/></td></tr>
    <tr><td colspan="3"><img src="images/green.gif" height="7" width="20"></td></tr>
    <tr><td align="left" width="33%">0%</td><td align="middle" width="34%">50%</td><td align="right" width="33%">100%</td></tr>
    </table>
    </td></tr>
    </table>
   </td>
   <td valign="top" align="center">
    <table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="70%" border="1">
    <tr><th colspan="2">
    <mm:write referid="folderaction">
        <mm:compare value="delete"><fmt:message key="DeleteMSGfromFolder"/> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:compare>
        <mm:compare value="email"><fmt:message key="EmailMSGtoFolder"/> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:compare>
        <mm:compare value="move"><fmt:message key="MoveMSGtoFolder"/>Moving message to a different folder</mm:compare>
        <mm:compare value="forward"><fmt:message key="ForwardMSGtoMember"/>Forward this message to other poster</mm:compare>
    </mm:write>
    </th></tr>
    <mm:present referid="mailboxid">
    <mm:node referid="messageid">
    <tr>
    <td width="50%" align="center" colspan="2">
        <mm:write referid="folderaction">
        <mm:compare value="delete">
        <br />
        <fmt:message key="SureWantDelete1"/> '<b><mm:field name="subject" /></b>'
        <fmt:message key="SureWantDelete2"/> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        <mm:compare value="move">
        <br />
        <fmt:message key="ForwardFromFolderTo1"/> '<b><mm:field name="subject" /></b>'
        <fmt:message key="ForwardFromFolderTo2"/> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>'
        <fmt:message key="ForwardFromFolderTo3"/> ?
        <br /><br />
        </mm:compare>
        <mm:compare value="email">
        <br />
        <fmt:message key="EmailTo1"/> '<b><mm:field name="subject" /></b>'
        <fmt:message key="EmailTo2"/> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        <mm:compare value="forward">
        <br />
        <fmt:message key="EmailTo1"/> '<b><mm:field name="subject" /></b>'
        <fmt:message key="EmailTo2"/> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        </mm:write>
    </td>
    </tr>
  <tr><td>
  <form action="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid" />" method="post">
    <p />
    <center>
    <input type="hidden" name="messageid" value="<mm:write referid="messageid" />" />
    <mm:write referid="folderaction">
    <mm:compare value="delete">
        <input type="hidden" name="action" value="removeprivatemessage" />
        <input type="hidden" name="foldername" value="<mm:node referid="mailboxid"><mm:field name="name" /></mm:node>" />
        <input type="submit" value="<fmt:message key="YesRemove"/>"> 
    </mm:compare>
    <mm:compare value="forward"><input type="submit" value="<fmt:message key="YesEmailThis"/>"> </mm:compare>
    <mm:compare value="move"><input type="submit" value="<fmt:message key="YesMoveIt"/>"> </mm:compare>
    <mm:compare value="email"><input type="submit" value="<fmt:message key="YesEmailMeIt"/>"> </mm:compare>
    </mm:write>
    </form>
    </td>
    <td>
    <form action="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid,messageid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<fmt:message key="OopsNo"/>">
    </form>
    </td>
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
</fmt:bundle>
</mm:cloud>
