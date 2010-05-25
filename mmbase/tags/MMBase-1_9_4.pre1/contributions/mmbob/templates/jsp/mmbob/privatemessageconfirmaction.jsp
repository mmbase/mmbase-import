<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="boxname">Inbox</mm:import>
<mm:import externid="mailboxid" />
<mm:import externid="messageid" />
<mm:import externid="folderaction" />
<mm:import externid="pathtype">privatemessages</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>

<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">

<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" style="margin-top : 20px;" width="95%">
 <tr>
   <td width="160" valign="top">
    <table cellpadding="0" width="150">
    <tr><td>
    <table cellpadding="0" class="list" cellspacing="0" width="150">
    <tr><th><mm:write referid="mlg.Folder" /></th></tr>
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
    <tr><th><mm:write referid="mlg.Add_folder" /></th></tr>
    <tr><td><input name="newfolder" style="width: 98%" /></td></tr>
    </table>
    </form>
    </td></tr>
    <tr><td>
        <table cellpadding="0" class="list" style="margin-top : 20px;" cellspacing="0" width="150">
        <mm:import id="barsize">150</mm:import>
        <mm:nodefunction set="mmbob" name="getQuotaInfo" referids="forumid,posterid,barsize">
        <tr><th colspan="3"><mm:write referid="mlg.PM_Quota" /></th></tr>
        <tr><td colspan="3"><mm:write referid="mlg.You_are_using" /> <mm:field name="quotausedpercentage" />% <mm:write referid="mlg.of_your_quota" /></td></tr>
        <tr><td colspan="3"><img src="images/<mm:field name="quotawarning" />.gif" height="7" width="<mm:field name="quotausedbar" />"></td></tr>
        <tr><td align="left" width="33%">0%</td><td align="middle" width="34%">50%</td><td align="right" width="33%">100%</td></tr>
        </mm:nodefunction>
        </table>

    </td></tr>
    </table>
   </td>
   <td valign="top" align="center">
    <table cellpadding="0" class="list" style="margin-top : 2px;" cellspacing="0" width="70%" border="1">
    <tr><th colspan="2">
    <mm:write referid="folderaction">
        <mm:compare referid2="mlg.delete"><mm:write referid="mlg.Delete_message_from" /> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node> <mm:write referid="mlg.folder" /></mm:compare>
        <mm:compare referid2="mlg.email"><mm:write referid="mlg.Email_message_to" /> <mm:node referid="mailboxid"><mm:field name="name" /></mm:node> <mm:write referid="mlg.folder" /></mm:compare>
        <mm:compare referid2="mlg.move"><mm:write referid="mlg.Move_message_to_different_folder" /></mm:compare>
        <mm:compare referid2="mlg.forward"><mm:write referid="mlg.Forward_message_to_other_member" /></mm:compare>
    </mm:write>
    </th></tr>
    <mm:present referid="mailboxid">
    <mm:node referid="messageid">
    <tr>
    <td width="50%" align="center" colspan="2">
        <mm:write referid="folderaction">
        <mm:compare referid2="mlg.delete">
          <br />
          <mm:write referid="mlg.Delete"/> '<b><mm:field name="subject" /></b>'
          <mm:write referid="mlg.from_folder"/> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>'.
                  <mm:write referid="mlg.Are_you_sure"/>
          <br /><br />
        </mm:compare>
        <mm:compare referid2="mlg.move">
          <br />
          <mm:write referid="mlg.Move"/> '<b><mm:field name="subject" /></b>'
          <mm:write referid="mlg.from_folder"/> '<b><mm:node referid="mailboxid"><mm:field name="name" /></mm:node></b>' ?
          <mm:write referid="mlg.to_mailbox"/> **<mm:write referid="mlg.Not_implemented"/>**
          <br /><br />
        </mm:compare>
        <mm:compare referid2="mlg.email">
          <br />
          <mm:write referid="mlg.Email"/> '<b><mm:field name="subject" /></b>'
          <mm:write referid="mlg.to_your_real_email_account"/> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
          <br /><br />
        </mm:compare>
        <mm:compare referid2="mlg.forward">
          <br />
          <mm:write referid="mlg.Email"/>  '<b><mm:field name="subject" /></b>'
          <mm:write referid="mlg.to_your_real_email_account"/> '<b><mm:node referid="posterid"><mm:field name="email" /></mm:node></b>' ?
        <br /><br />
        </mm:compare>
        </mm:write>
    </td>
    </tr>
  <tr><td align="center">
  <form action="<mm:url page="privatemessages.jsp" referids="forumid,mailboxid" />" method="post">
    <p />
    <input type="hidden" name="messageid" value="<mm:write referid="messageid" />" />
    <mm:write referid="folderaction">
    <mm:compare referid2="mlg.delete">
        <input type="hidden" name="action" value="removeprivatemessage" />
        <input type="submit" value="<mm:write referid="mlg.Ok"/>, <mm:write referid="mlg.delete"/>">
    </mm:compare>
    <mm:compare referid2="mlg.forward"><input type="submit" value="<mm:write referid="mlg.Ok"/>, <mm:write referid="mlg.forward"/>"> </mm:compare>
    <mm:compare referid2="mlg.move"><input type="submit" value="<mm:write referid="mlg.Ok"/>, <mm:write referid="mlg.move"/>"> </mm:compare>
    <mm:compare referid2="mlg.email"><input type="submit" value="<mm:write referid="mlg.Ok"/>, <mm:write referid="mlg.email"/>"> </mm:compare>
    </mm:write>
    </form>
    </td>
    <td align="center">
    <form action="<mm:url page="privatemessage.jsp" referids="forumid,mailboxid,messageid" />" method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
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

</div>
<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>

</div>

</body>
</html>

</mm:locale>
</mm:cloud>
