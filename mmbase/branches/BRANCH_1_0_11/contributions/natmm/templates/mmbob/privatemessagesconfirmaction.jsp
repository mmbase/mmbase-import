<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="css/mmbase-dev.css" />
   <title>MMBase Forum</title>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</head>
<mm:cloud>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="boxname">Inbox</mm:import>
<mm:import externid="mailboxid" />
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
    <tr><th>Folder</th></tr>
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
    <tr><th>Add Folder</th></tr>
    <tr><td><input name="newfolder" style="width: 98%" /></td></tr>
    </table>
    </form>
    </td></tr>
    <tr><td>
    <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
    <tr><th colspan="3">PM Quota</th></tr>
    <tr><td colspan="3">You are using 10% of your quota</td></tr>
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
        <mm:compare value="delete mailbox">Delete message from <mm:node referid="mailboxid"><mm:field name="name" /></mm:node> folder</mm:compare>
        <mm:compare value="forward">Forward this message to other poster</mm:compare>
    </mm:write>
    </th></tr>
    <mm:present referid="mailboxid">
    <tr>
    <td width="50%" align="center" colspan="2">
        <mm:write referid="folderaction">
        <mm:compare value="delete mailbox">
        <br />
        <mm:node referid="mailboxid">
        Sure you want to delete mail folder '<b><mm:field name="name" /></b>' ?
        <br /><br />
        </mm:node>
        </mm:compare>
        </mm:write>
    </td>
    </tr>
  <tr><td>
    <form action="<mm:url page="privatemessages.jsp" referids="forumid"></mm:url>" method="post">
    <p />
    <center>
    <mm:write referid="folderaction">
    <input type="hidden" name="action" value="removefolder">
    <input type="hidden" name="foldername" value="<mm:node referid="mailboxid"><mm:field name="name" /></mm:node>">
    <mm:compare value="delete mailbox"><input type="submit" value="Yes, Remove"> </mm:compare>
    </mm:write>
    </form>
    </td>
    <td>
    <form action="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid"></mm:url>" method="post">
    <p />
    <center>
    <input type="submit" value="Oops, No">
    </form>
    </td>
    </tr>

    </mm:present>
    </table>
    </form>
   </td>
 </tr>
</table>
</mm:cloud>
</center>
</html>
