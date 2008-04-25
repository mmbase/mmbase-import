<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud  rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Database Connections</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="databases">
<tr>
  <th class="header">Connection</th>
  <th class="header">Database</th>
  <th class="header">State</th>
  <th class="header">Last Query</th>
  <th class="header">Query #</th>
</tr>
<mm:nodelistfunction module="jdbc" name="CONNECTIONS">
  <tr>
    <td class="data"><mm:index /></td>
    <td class="data"><mm:field name="item1" /></td>
    <td class="data"><mm:field name="item2" /></td>
    <td class="data"><mm:field name="item3" /></td>
    <td class="data"><mm:field name="item4" /></td>
  </tr>
</mm:nodelistfunction>
<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../databases.jsp"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Database Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
