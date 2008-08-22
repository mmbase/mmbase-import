<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" />
   <title><di:translate key="mmbob.mmbaseforum" /></title>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="boxname"><di:translate key="mmbob.inbox" /></mm:import>
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
    <form action="" METHOD="POST">
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th><di:translate key="mmbob.addfolder" /></th></tr>
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
   <td valign="top" align="center">
    <table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="70%" border="1">
    <tr><th colspan="2">
    <mm:write referid="folderaction">
        <mm:compare value="delete"><di:translate key="mmbob.deletemsgfromfolder" /> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:compare>
        <mm:compare value="email"><di:translate key="mmbob.emailmsgtofolder" /> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node></mm:compare>
        <mm:compare value="move"><di:translate key="mmbob.movemsgtofolder" />Moving message to a different folder</mm:compare>
        <mm:compare value="forward"><di:translate key="mmbob.forwardmsgtomember" />Forward this message to other poster</mm:compare>
    </mm:write>
    </th></tr>
    <mm:present referid="mailboxid">
    <mm:node referid="messageid">
    <tr>
    <td width="50%" align="center" colspan="2">
        <mm:write referid="folderaction">
        <mm:compare value="delete">
        <br />
        <di:translate key="mmbob.surewantdelete1" /> '<b><mm:field name="subject" /></b>'
        <di:translate key="mmbob.surewantdelete2" /> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        <mm:compare value="move">
        <br />
        <di:translate key="mmbob.forwardfromfolderto1" /> '<b><mm:field name="subject" /></b>'
        <di:translate key="mmbob.forwardfromfolderto2" /> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>'
        <di:translate key="mmbob.forwardfromfolderto3" /> ?
        <br /><br />
        </mm:compare>
        <mm:compare value="email">
        <br />
        <di:translate key="mmbob.emailto1" /> '<b><mm:field name="subject" /></b>'
        <di:translate key="mmbob.emailto2" /> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        <mm:compare value="forward">
        <br />
        <di:translate key="mmbob.emailto1" /> '<b><mm:field name="subject" /></b>'
        <di:translate key="mmbob.emailto2" /> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
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
        <input type="submit" value="<di:translate key="mmbob.yesremove" />"> 
    </mm:compare>
    <mm:compare value="forward"><input type="submit" value="<di:translate key="mmbob.yesemailthis" />"> </mm:compare>
    <mm:compare value="move"><input type="submit" value="<di:translate key="mmbob.yesmoveit" />"> </mm:compare>
    <mm:compare value="email"><input type="submit" value="<di:translate key="mmbob.yesemailmeit" />"> </mm:compare>
    </mm:write>
    </form>
    </td>
    <td>
    <form action="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid,messageid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<di:translate key="mmbob.oopsno" />">
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
</mm:cloud>
