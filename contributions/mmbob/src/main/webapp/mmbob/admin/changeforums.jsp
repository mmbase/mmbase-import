<%@ include file="../jspbase.jsp" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import id="forumid"></mm:import>
<%@ include file="../thememanager/loadvars.jsp" %>

<mm:import externid="lang">en</mm:import>
<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
</div>

<div class="bodypart">

<mm:nodefunction set="mmbob" name="getForumsConfig">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="55%">
  <tr><th colspan="3">Login instellingen</th></tr>
  <form action="<mm:url page="../forums.jsp" />" method="post">
    <input type="hidden" name="action" value="changeconfigs">
    <tr><th>Login System</th>
        <td colspan="2" align="left"><select name="loginsystemtype">
        <mm:field name="loginsystemtype">
        <mm:compare value="http">
        <option>http
        <option>entree
        </mm:compare>
        <mm:compare value="entree">
        <option>entree
        <option>http
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>Login Mode</th>
        <td colspan="2" align="left"><select name="loginmodetype">
        <mm:field name="loginmodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>LogoutMode</th>
        <td colspan="2" align="left"><select name="logoutmodetype">
        <mm:field name="logoutmodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>GuestReadMode</th>
        <td colspan="2" align="left"><select name="guestreadmodetype">
        <mm:field name="guestreadmodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>GuestWriteMode</th>
        <td colspan="2" align="left"><select name="guestwritemodetype">
        <mm:field name="guestwritemodetype">
        <mm:compare value="open">
        <option>open
        <option>closed
        </mm:compare>
        <mm:compare value="closed">
        <option>closed
        <option>open
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>AvatarUpload</th>
        <td colspan="2" align="left"><select name="avatarsuploadenabled">
        <mm:field name="avatarsuploadenabled">
        <mm:compare value="true">
        <option value="true">on
        <option value="false">off
        </mm:compare>
        <mm:compare value="false">
        <option value="false">off
        <option value="true">on
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>AvatarGallery</th>
        <td colspan="2" align="left"><select name="avatarsgalleryenabled">
        <mm:field name="avatarsgalleryenabled">
        <mm:compare value="true">
        <option value="true">on
        <option value="false">off
        </mm:compare>
        <mm:compare value="false">
        <option value="false">off
        <option value="true">on
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>ContactInfo</th>
        <td colspan="2" align="left"><select name="contactinfoenabled">
        <mm:field name="contactinfoenabled">
        <mm:compare value="true">
        <option value="true">on
        <option value="false">off
        </mm:compare>
        <mm:compare value="false">
        <option value="false">off
        <option value="true">on
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>Smileys</th>
        <td colspan="2" align="left"><select name="smileysenabled">
        <mm:field name="smileysenabled">
        <mm:compare value="true">
        <option value="true">on
        <option value="false">off
        </mm:compare>
        <mm:compare value="false">
        <option value="false">off
        <option value="true">on
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>PrivateMessages</th>
        <td colspan="2" align="left"><select name="privatemessagesenabled">
        <mm:field name="privatemessagesenabled">
        <mm:compare value="true">
        <option value="true">on
        <option value="false">off
        </mm:compare>
        <mm:compare value="false">
        <option value="false">off
        <option value="true">on
        </mm:compare>
        </mm:field>
        </td>
    </tr>
    <tr><th>Postings Per Page</th>
        <td colspan="2" align="left">
        <input size="5" name="postingsperpage" value="<mm:field name="postingsperpage" />" />
        </td>
    </tr>
  <th>&nbsp;</th>
<td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Save"/>">
    </form>
    </td>
  <td>
    <form action="<mm:url page="../forums.jsp" />" method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
  </form>
  </td>
  </tr>
</table>
</mm:nodefunction>
</div>

<div class="footer">
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

