<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

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

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <tr><th colspan="3"><mm:write referid="mlg.Delete"/> <mm:write referid="mlg.topic"/>: <mm:node referid="postthreadid">'<mm:field name="subject" />'</mm:node> </th></tr>
  <tr><td colspan="3"><mm:write referid="mlg.Are_you_sure"/></td></tr>
  <tr><td>
  <form action="<mm:url page="postarea.jsp" referids="forumid,postareaid,postthreadid" />" method="post">
    <input type="hidden" name="moderatorcheck" value="true">
    <input type="hidden" name="action" value="removepostthread">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Yes_delete"/>">
        </center>
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

