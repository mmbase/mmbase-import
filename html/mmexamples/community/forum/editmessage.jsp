<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud name="mmbase" method="http" logon="admin">
<mm:import externid="channel" type="parameters" />
<mm:import externid="thread" type="parameters" />
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../../share/community/css/mmbase.css" />
</head>
<body class="basic">
<mm:node number="${thread}">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">Edit/Moderate a message</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
<br />
<a name="post" />
<mmcommunity:getinfo key="name" skiponempty="true" jspvar="infoname">
     <p><em>Originally posted by <%=infoname%></em> on
        <mm:field name="day(timestampsec)" />
        <mm:field name="month(timestampsec)" />
        <mm:field name="year(timestampsec)" /></p>
</mmcommunity:getinfo>
<form method="post" action="<mm:url page="updatemessage.jsp" referids="channel,thread" />">
<input type="checkbox" name="username" value="moderator" />Change original poster to 'moderator'
<input type="text" name="subject" size="75" maxlength="80" value="<mm:field name="subject" />" />
<textarea name="body" cols="75" rows="12" wrap="on"><mm:field name="body" /></textarea><br />
<input type="submit" name="action" value="edit message">
</form>
  &nbsp;
 </td>
</tr>
<tr>
<td class="data">ADMIN: Remove this message and all its descendants</td>
<form action="<mm:url page="removemessage.jsp" referids="channel,thread" />" method="POST">
 <td class="linkdata" >
    <input type="submit" value="REMOVE" />
 </td>
</form>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="navigate"><a href="<mm:url page="forum.jsp" referids="channel" />" ><img src="../../../share/community/images/back.gif" alt="back" border="0" align="left" /></a></td>
<td class="data">Return to forum</td>
</tr>
</table>
</mm:node>
</body>
</html>
</mm:cloud>
