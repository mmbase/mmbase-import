<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud name="mmbase">
<mm:import externid="channel" from="parameters"/>
<mm:import externid="thread" from="parameters"/>
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic"><mm:node number="${thread}">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">Post a message:</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
<a name="post" />
<form method="post" action="<mm:url page="postmessage.jsp" referids="channel,thread" />">
Name : <input type="text" name="username" value="" /> <br />
<input type="text" name="subject" size="75" maxlength="80" value="<mm:field name="resubject" />" />
<textarea name="body" cols="75" rows="12" wrap="on"></textarea><br />
<input type="submit" name="action" value="post message">
</form>
</mm:node>
  &nbsp;
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="<mm:url page="forum.jsp" referids="channel" />" ><img src="../../images/back.gif" alt="back" border="0" align="left" /></a></td>
<td class="data">Return to forum</td>
</tr>

</table>
</body></html>
</mm:cloud>
