<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud name="mmbase" method="http" logon="admin">
<mm:import externid="channel" type="parameters" />
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../../share/community/css/mmbase.css" />
</head>
<body class="basic">
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">Administer forum</th>
</tr>
<tr>
<td class="data">ADMIN: Remove all messages</td>
<form action="<mm:url page="removeallmessages.jsp" referids="channel" />" method="POST">
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
</body>
</html>
</mm:cloud>
