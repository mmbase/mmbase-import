<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>

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

<!-- moderator check -->
<%-- Administrative / Moderative functions --%>
<mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
   <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
</mm:nodefunction>

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

<mm:compare referid="ismoderator" value="false">
  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
    <tr><th><mm:write referid="mlg.Edit_postthread" /></th></tr>
    <tr><td><font color="red"><b><mm:write referid="mlg.Access_denied" /></font></b></td></tr>
  </table>
</mm:compare>

<mm:compare referid="ismoderator" value="true">

  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
    <mm:node referid="postthreadid">
    <tr><th colspan="3"><mm:write referid="mlg.Edit_postthread" /></th></tr>
    <form action="<mm:url page="postarea.jsp" referids="forumid,postareaid,postthreadid" />" method="post">
    <tr><th width="200"><mm:write referid="mlg.Status" /></th><td colspan="2" align="middle">
        <select name="state">
        <mm:field name="state">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg.normal" />
        <option value="closed" <mm:compare value="closed">selected</mm:compare>><mm:write referid="mlg.closed" />
        <option value="pinned" <mm:compare value="pinned">selected</mm:compare>><mm:write referid="mlg.pinned" />
        <option value="pinnedclosed" <mm:compare value="pinnedclosed">selected</mm:compare>><mm:write referid="mlg.pinned" /><mm:write referid="mlg.closed" />
        </mm:field>
        </select>
    </td></th>
    <tr><th><mm:write referid="mlg.Mood"/></th><td align="middle" colspan="2">
        <select name="mood">
        <mm:field name="mood">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg.normal" />
        <option value="mad" <mm:compare value="mad">selected</mm:compare>><mm:write referid="mlg.mad"/>
        <option value="happy" <mm:compare value="happy">selected</mm:compare>><mm:write referid="mlg.happy"/>
        <option value="sad" <mm:compare value="sad">selected</mm:compare>><mm:write referid="mlg.sad"/>
        <option value="question" <mm:compare value="question">selected</mm:compare>><mm:write referid="mlg.question"/>
        <option value="warning" <mm:compare value="warning">selected</mm:compare>><mm:write referid="mlg.warning"/>
        <option value="joke" <mm:compare value="joke">selected</mm:compare>><mm:write referid="mlg.joke"/>
        <option value="idea" <mm:compare value="idea">selected</mm:compare>><mm:write referid="mlg.idea"/>
        <option value="suprised" <mm:compare value="suprised">selected</mm:compare>><mm:write referid="mlg.surprised"/>
        </mm:field>
        </select>
    <tr><th>Type</th><td align="middle" colspan="2">
        <select name="ttype">
        <mm:field name="ttype">
        <option value="normal" <mm:compare value="normal">selected</mm:compare>><mm:write referid="mlg.normal"/>
        <option value="note" <mm:compare value="note">selected</mm:compare>><mm:write referid="mlg.note"/>
        <option value="faq" <mm:compare value="faq">selected</mm:compare>><mm:write referid="mlg.faq"/>
        <option value="announcement" <mm:compare value="announcement">selected</mm:compare>><mm:write referid="mlg.announcement"/>
        </select>
        </mm:field>
        </td></th>

    <tr><th>&nbsp;</th><td align="center">
    <input type="hidden" name="action" value="editpostthread">
    <input type="submit" value="<mm:write referid="mlg.Save"/>">
    </td>
    <td align="center">
    </mm:node>
        </form>
    <form action="<mm:url page="postarea.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    </mm:url>"
    method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
    </form>
    </td>
    </tr>
  </table>
</mm:compare>

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
