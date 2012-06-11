<%@ include file="jspbase.jsp" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="thememanager/loadvars.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>

<mm:import externid="action" />
<mm:present referid="action">


<mm:compare value="changeconfigs" referid="action">
    <mm:import externid="loginsystemtype" />
    <mm:import externid="loginmodetype" />
    <mm:import externid="logoutmodetype" />
    <mm:import externid="guestreadmodetype" />
    <mm:import externid="guestwritemodetype" />
    <mm:import externid="avatarsuploadenabled" />
    <mm:import externid="avatarsgalleryenabled" />
    <mm:import externid="contactinfoenabled" />
    <mm:import externid="smileysenabled" />
    <mm:import externid="privatemessagesenabled" />
    <mm:import externid="postingsperpage" />
    <mm:booleanfunction set="mmbob" name="changeForumsConfig" referids="loginsystemtype,loginmodetype,logoutmodetype,guestreadmodetype,guestwritemodetype,avatarsuploadenabled,avatarsgalleryenabled,contactinfoenabled,smileysenabled,privatemessagesenabled,postingsperpage" >
    </mm:booleanfunction>
</mm:compare>

</mm:present>
<!-- end action check -->

<body>

<div class="header">
</div>

<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%" align="center">
   <tr><th>Forum naam</th><th>berichten</th><th>views</th><th>gebruikers</th><th>threadsloaded</th><th>memory size</th></tr>
  <mm:nodelistfunction set="mmbob" name="getForums">
            <tr>
            <td align="left"><a href="index.jsp?forumid=<mm:field name="id" />"><mm:field name="name" /></a></td>
            <td><mm:field name="postcount" /></td>
            <td><mm:field name="viewcount" /></td>
            <td><mm:field name="posterstotal" /></td>
            <td><mm:field name="postthreadloadedcount" /></td>
                        <td><mm:field name="memorysize" /></td>
            </tr>
  </mm:nodelistfunction>
</table>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%" align="center">
    <tr><th align="left">Administratie Functies</th></tr>
    <td align="left">
    <p />
    <a href="<mm:url page="admin/newforum.jsp"></mm:url>">Forum toevoegen</a><br />
    <a href="<mm:url page="admin/removeforum.jsp"></mm:url>">Forum verwijderen</a><br />
    <a href="<mm:url page="admin/changeforums.jsp"></mm:url>">Forums settings</a><br />
    <a href="<mm:url page="/mmbase/thememanager/index.jsp"></mm:url>" target="thememanager">Thememanager</a><br />
    <a href="<mm:url page="/mmbase/mlg/index.jsp"></mm:url>" target="mlg">MultiLanguage</a><br />
    <a href="<mm:url page="generate/index.jsp"></mm:url>" target="gentools">Generate tools</a><br />
    <a href="<mm:url page="admin/stats.jsp"></mm:url>">Forums statistics</a><br />
    <p />
    </td>
    </tr>
    </table>
</mm:cloud>
</div>

<div class="footer">
</div>

</body>
</html>
