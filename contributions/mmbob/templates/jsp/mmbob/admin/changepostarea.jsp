<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>
<mm:import externid="postareaid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- login part -->
  <%@ include file="../getposterid.jsp" %>
<!-- end login part -->
<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
        <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
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
  <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
  <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
  <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
  </mm:nodefunction>
  <mm:include page="../path.jsp?type=postarea2" referids="logoutmodetype,forumid,posterid,active_nick" />

<mm:compare referid="isadministrator" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.Change_existing_area" /></th></tr>

  <mm:node number="$postareaid">
  <form action="<mm:url page="changepostarea.jsp" referids="forumid,postareaid" />" method="post">
    <tr><th><mm:write referid="mlg.Name"/></th><td colspan="2">
    <input name="name" size="70" value="<mm:field name="name" />" style="width: 100%">
    </td></tr>
    <tr><th><mm:write referid="mlg.Description"/></th><td colspan="2">
    <textarea name="description" rows="5" style="width: 100%"><mm:field name="description" /></textarea>
    </td></tr>
        <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="changepostarea">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Save"/>">
    </form>
    </td>
    </mm:node>
    <td>
    <form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
    </form>
    </td>
    </tr>

</table>


<mm:nodefunction set="mmbob" name="getPostAreaConfig" referids="forumid,posterid,postareaid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="55%">
  <tr><th colspan="3">Postarea instellingen</th></tr>
  <form action="<mm:url page="changepostarea.jsp">
        <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
                </mm:url>" method="post">
        <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="changepostareaconfig">
    <tr><th width="25%">GuestReadMode</th>
        <td colspan="2" align="left"><select name="guestreadmodetype">
        <mm:field name="guestreadmodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        <option>default
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        <option>default
        </mm:compare>
        <mm:compare value="default">
        <option>default
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </select>
        </td>
    </tr>
    <tr><th>GuestWriteMode</th>
        <td colspan="2" align="left"><select name="guestwritemodetype">
        <mm:field name="guestwritemodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        <option>default
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        <option>default
        </mm:compare>
        <mm:compare value="default">
        <option>default
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </select>
        </td>
    </tr>
    <tr><th>Thread start level</th>
        <td colspan="2" align="left"><select name="threadstartlevel">
        <mm:field name="threadstartlevel">
        <mm:compare value="">
        <option>default
        <option>all
        <option>moderator
        </mm:compare>
        <mm:compare value="default">
        <option>default
        <option>all
        <option>moderator
        </mm:compare>
        <mm:compare value="all">
        <option>all
        <option>default
        <option>moderator
        </mm:compare>
        <mm:compare value="moderator">
        <option>moderator
        <option>all
        <option>default
        </mm:compare>
        </mm:field>
        </select>
        </td>
    </tr>
    <tr><th>PostArea position</th>
        <td colspan="2" align="left"><input size="4" name="position" value="<mm:field name="position" />">
        </td>
    </tr>
  <th>&nbsp;</th>
<td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Save"/>">
    </form>
    </td>
  <td>
    <form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
  </form>
  </td>
  </tr>
</table>
</mm:nodefunction>
</mm:compare>
<mm:compare referid="isadministrator" value="false">
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

