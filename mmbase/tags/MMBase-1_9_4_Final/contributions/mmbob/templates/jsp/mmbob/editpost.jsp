<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postingid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>

<%-- login part --%>
<%@ include file="getposterid.jsp" %>

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<%-- action check --%>
 <c:if test="${not empty param.action}"><mm:include page="actions.jsp" /></c:if>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">
<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
<mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
<mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
<mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
<mm:import id="active_firstname"><mm:field name="active_firstname" /></mm:import>
<mm:import id="active_lastname"><mm:field name="active_lastname" /></mm:import>
<mm:include page="path.jsp?type=postthread" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>
<mm:nodefunction set="mmbob" name="getPosting" referids="forumid,postareaid,postthreadid,postingid,posterid,imagecontext">
    <mm:field name="maychange" id="maychange" write="false" />
</mm:nodefunction>

<mm:compare referid="maychange" value="true">
<mm:node referid="postingid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.Edit_message" /></th></tr>
  <form action="<mm:url page="thread.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    <mm:param name="postthreadid" value="$postthreadid" />
    <mm:param name="postingid" value="$postingid" />
    </mm:url>" method="post" name="posting">
    <tr><th><mm:write referid="mlg.Name"/></th><td colspan="2">
        <mm:compare referid="posterid" value="-1" inverse="true">
        <mm:write referid="active_nick" /> (<mm:write referid="active_firstname" /> <mm:write referid="active_lastname" />)
        <input name="poster" type="hidden" value="<mm:write referid="active_nick" />" >
        </mm:compare>
        <mm:compare referid="posterid" value="-1">
        <input name="poster" size="32" value="gast" >
        </mm:compare>
    </td></tr>
    <tr><th width="150"><mm:write referid="mlg.Topic"/></th><td colspan="2"><input name="subject" style="width: 100%" value="<mm:field name="subject" />" ></td></th>
    <tr><th valign="top" align="center"><mm:write referid="mlg.Message"/>

<mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
<mm:field name="smileysenabled"><mm:compare value="true">
<table><tr><th width="100"><%@ include file="includes/smilies.jsp" %></th></tr></table>
</mm:compare></mm:field>
</mm:nodefunction>

</th><td colspan="2"><textarea name="body" rows="20" style="width: 100%"><mm:formatter xslt="xslt/posting2textarea.xslt"><mm:field name="body" /></mm:formatter></textarea>
</td></tr>
    <tr><th>&nbsp;</th><td align="center">
    <input type="hidden" name="action" value="editpost">
    <input type="submit" value="<mm:write referid="mlg.Save"/>">
    </form>
    </td>
    <td align="center">
    <form action="<mm:url page="thread.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    <mm:param name="postthreadid" value="$postthreadid" />
    </mm:url>"
    method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
    </form>
    </td>
    </tr>
</table>
</mm:node>
</mm:compare>
<mm:compare referid="maychange" value="false">
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
        <tr><th>MMBob system error</th></tr>
        <tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
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
