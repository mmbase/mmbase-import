<%@ include file="inc.jsp" %>
<body class="basic">
<%@ page errorPage="actionerror.jsp" %>
<mm:cloud name="mmbase" method="http" logon="admin">
<mm:import externid="channel" from="parameters" />
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Forum</title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
</head>
<body class="basic">
<mmcommunity:deleteall channel="${channel}" />
<table summary="forum threads" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">Result of your action</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
     All messages are removed.
  </td></tr>
<tr><td>&nbsp;</td></tr>
<tr>
<td class="navigate"><a href="<mm:url page="forum.jsp" referids="channel" />" ><img src="../../images/back.gif" alt="back" border="0" align="left" /></a></td>
<td class="data">Return to forum</td>
</tr>
</table>
</mm:cloud>
</body></html>

