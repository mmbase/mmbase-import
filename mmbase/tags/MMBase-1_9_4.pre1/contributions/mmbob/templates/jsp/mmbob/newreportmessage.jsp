<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="postingid" />

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

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.send"/> <mm:write referid="mlg.To" /> <mm:write referid="mlg.Moderators"/> </th></tr>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,postingid" />" method="post">
    <tr><th><mm:write referid="mlg.To"/> <mm:write referid="mlg.Moderators"/></th><td colspan="2">
        <mm:node number="$postingid">
        <mm:import id="to"><mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid"><mm:first><mm:field name="account" /></mm:first><mm:first inverse="true">,<mm:field name="account" /></mm:first></mm:nodelistfunction></mm:import>
        <mm:write referid="to" />
        <input name="to" type="hidden" value="<mm:write referid="to" />">
        <input name="poster" type="hidden" value="<mm:node referid="posterid"><mm:field name="account" /></mm:node>">
    </td></tr>
    <mm:import id="subject">mod report : <mm:node referid="postareaid"><mm:field name="name" /></mm:node> / <mm:field name="subject" /> /  <mm:write referid="postingid" /> (<mm:field name="c_poster" />)</mm:import>
    <tr><th><mm:write referid="mlg.Subject"/></th><td colspan="2"><mm:write referid="subject" /></td></th>
    </mm:node>
    <input type="hidden" name="subject" value="<mm:write referid="subject" />" />
    <tr><th><mm:write referid="mlg.Message" /></th><td colspan="2"><textarea name="body" rows="20" style="width: 100%"></textarea></td></tr>
    <tr><th>&nbsp;</th><td>
    <input type="hidden" name="action" value="newprivatemessage">
    <center><input type="submit" value="<mm:write referid="mlg.Send"/> <mm:write referid="mlg.message"/>"></center>
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
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
        </center>
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
</mm:content>
</mm:cloud>
