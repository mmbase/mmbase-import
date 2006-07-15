<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" rank="administrator">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Databases</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="databases">
<% 
   java.util.Map params = new java.util.Hashtable();
if( org.mmbase.module.core.MMBase.getMMBase().getStorageManagerFactory() == null) { %>
<tr><th class="header" colspan="5">Database Overview</th></tr>
<tr>
  <td class="multidata" colspan="5">
  <p>This overview lists all database systems supported by this system, as well as
     all connection pools (which administrate the actual database connections).
  </p>
  </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <th class="header">Name</th>
  <th class="header">Version</th>
  <th class="header">Installed</th>
  <th class="header">Maintainer</th>
  <th class="navigate">View</th>
</tr>
<mm:nodelistfunction module="mmadmin" name="DATABASES">
  <tr>
    <td class="data"><mm:field id="database" name="item1" /></td>
    <td class="data"><mm:field name="item2" /></td>
    <td class="data"><mm:field name="item3" /></td>
    <td class="data"><mm:field name="item4" /></td>
    <td class="navigate">    
      <a href="<mm:url referids="database" page="database/actions.jsp" />">
      <img src="<mm:url page="/mmbase/style/images/search.gif" />" border="0" alt="view" />
    </a>
  </td>
</tr>
</mm:nodelistfunction>
<tr><td>&nbsp;</td></tr>

<% } %>
<mm:hasfunction module="jdbc" name="POOLS">
<tr>
  <th class="header" colspan="2">Pool Name</th>
  <th class="header">Size</th>
  <th class="header">Connections Created</th>
  <th class="navigate">View</th>
</tr>
<mm:nodelistfunction module="jdbc" name="POOLS">
  <tr>
    <td class="data" colspan="2"><mm:field name="item1" id="item1" /></td>
    <td class="data"><mm:field name="item2" /></td>
    <td class="data"><mm:field name="item2" /></td>
    <td class="navigate">
      <a href="<mm:url referids="item1" page="database/connections.jsp" />"><img src="<mm:url page="/mmbase/style/images/search.gif" />" border="0" alt="next" /></a>
  </td>
</tr>
</mm:nodelistfunction>
<tr><td>&nbsp;</td></tr>
</mm:hasfunction>
<mm:hasfunction module="jdbc" name="POOL" inverse="true">
  <tr>
    <td>Function for database pool inspection not available</td>
  </tr>
</mm:hasfunction>

<tr class="footer">
    <td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
    <td class="data" colspan="4">Return to home page</td>
  </tr>
</table>
</body></html>
</mm:cloud>
