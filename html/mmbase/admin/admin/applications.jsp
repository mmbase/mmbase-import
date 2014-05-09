<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Applications</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="applications">
<tr>
<th class="header" colspan="6">Module Overview
</th>
</tr>
<tr>
  <td class="multidata" colspan="6">
  <p>This overview lists all applications known to this system.
  </p>
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <th class="header">Name</th>
  <th class="header">Version</th>
  <th class="header">Installed</th>
  <th class="header">Maintainer</th>
  <th class="header">Auto-Deploy</th>
  <th class="navigate">Manage</th>
</tr>
<mm:nodelistfunction module="mmadmin" name="APPLICATIONS">
<tr>
  <td class="data"><mm:field id="application" name="item1" /></td>
  <td class="data"><mm:field name="item2" /></td>
  <td class="data"><mm:field name="item3" /></td>
  <td class="data"><mm:field name="item4" /></td>
  <td class="data"><mm:field name="item5" /></td>
  <td class="navigate">
    <a href="<mm:url referids="application" page="application/actions.jsp" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" /></a>
  </td>
</tr>
</mm:nodelistfunction>
<tr><td>&nbsp;</td></tr>

  <tr class="footer">
    <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="5">Return to home page</td>
  </tr>
</table>
</body></html>
</mm:cloud>
