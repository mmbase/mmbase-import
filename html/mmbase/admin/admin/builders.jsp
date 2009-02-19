<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content expires="0">
<mm:cloud rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builders</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
<table summary="builders">
<tr>
<th class="header" colspan="5">Builder Overview <mm:cloudinfo type="rank" />
</th>
</tr>
<tr>
  <td class="multidata" colspan="5">
  <p>This overview lists all known builders.<br />
     The first list contains all builders that are currently 'active' (accessible through MMBase).<br />
     The second list (if available) lists all builders for which the definition is known, but which are currently inactive
     (and thus inaccessible).
  </p>
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <th class="header">Name</th>
  <th class="header">Version</th>
  <th class="header">Installed</th>
  <th class="header">Maintainer</th>
  <th class="navigate">Manage</th>
</tr>
<mm:nodelistfunction id="builders" module="mmadmin" name="BUILDERS">
  <mm:field name="item3">
    <mm:compare value="no" inverse="true">
      <tr>
        <td class="data"><mm:field id="builder" name="item1" /></td>
        <td class="data"><mm:field name="item2" /></td>
        <td class="data"><mm:field name="item3" /></td>
        <td class="data"><mm:field name="item4" /></td>
        <td class="navigate">
          <a href="<mm:url referids="builder" page="builder/actions.jsp" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" /></a>
        </td>
      </tr>
    </mm:compare>
  </mm:field>
</mm:nodelistfunction>
<mm:remove referid="builder" />
<tr><td>&nbsp;</td></tr>
<mm:listnodes referid="builders">
  <mm:field name="item3">
    <mm:compare value="no">
      <tr>
        <td class="data"><mm:field id="builder" name="item1" /></td>
        <td class="data"><mm:field name="item2" /></td>
        <td class="data"><mm:field name="item3" /></td>
        <td class="data"><mm:field name="item4" /></td>
        <td class="navigate">
          <a href="<mm:url referids="builder" page="builder/actions.jsp" />"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="next" border="0" /></a>
        </td>
      </tr>
    </mm:compare>
  </mm:field>
</mm:listnodes>
<tr><td>&nbsp;</td></tr>

  <tr class="footer">
    <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="4">Return to home page</td>
  </tr>
</table>
</body></html>
</mm:cloud>
</mm:content>