<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String database = request.getParameter("database"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Database <%=database%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="database actions">
<%
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
%>
<tr>
 <th class="header" colspan="5"><!-- Description --><%=database%> database</th>
</tr>
<!-- tr>
 <td class="multidata" colspan="5">
        <p>In one of the next MMBase-releases you'll find here detailed information about
            the <%=database%>-databasemodule.</p>&nbsp;
 </td>
</tr-->

<tr><td>&nbsp;</td></tr>

<%
    Module mmconfig=ContextProvider.getDefaultCloudContext().getModule("config");
    if (mmconfig!=null) {
        String check=mmconfig.getInfo("CHECK-databases-"+database);
%>
<tr>
<th class="header">Action</th>
  <th class="header" colspan="3">Status</th>
  <th class="navigate" >View</th>
</tr>
<tr>
 <td class="data">XML-check</td>
 <td class="data" colspan="3"><%=check%></td>
 <td class="linkdata" >
  <form action="<mm:url page="../config/details.jsp" />" method="POST" target="_xml">
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="databases" />
    <input type="hidden" name="target" value="<%=database%>" />
    <input type="image" src="<mm:url page="/mmbase/style/images/search.gif" />" alt="view" border="0"  />
  </form>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<% } %>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../databases.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="4">Return to Database Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
