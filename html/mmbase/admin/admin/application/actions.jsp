<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<% String app = request.getParameter("application"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Application <%=app%></title>
<link rel="stylesheet" type="text/css" href="../../style/css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="application actions">
<%
    Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
%>
<tr>
 <th class="header" colspan="4">Description of <%=app%></th>
</tr>
<tr>
 <td class="multidata" colspan="4">
        <p><%=mmAdmin.getInfo("DESCRIPTION-"+app,request,response)%></p>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header" colspan="2">Action</th>
  <th class="header" >&nbsp;</th>
  <th class="navigate">Confirm</th>
</tr>
<tr>
 <td class="data" colspan="2">Install <%=app%></td>
 <td class="data" >Version: <%=mmAdmin.getInfo("VERSION-"+app,request,response)%> </td>
 <td class="linkdata" >
  <form action="<mm:url page="result.jsp" />" method="POST">
   <input type="hidden" name="application" value="<%=app%>" />
   <input type="hidden" name="cmd" value="LOAD" />
   <input type="image" src="../../style/images/ok.gif" alt="OK" border="0"  />
  </form>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="<mm:url page="result.jsp" />" method="POST">
<tr>
<th class="header">Action</th>
  <th class="header">Path</th>
  <th class="header">Goal</th>
  <th class="navigate">Confirm</th>
</tr>
<tr>
 <td class="data" >Save <%=app%></td>
 <td class="data" ><input type="text" name="path" value="/tmp" size="80" /></td>
 <td class="data" ><select name="goal">
        <option selected="selected">backup</option>
    </select>
 </td>
 <td class="linkdata" >
   <input type="hidden" name="application" value="<%=app%>" />
   <input type="hidden" name="cmd" value="SAVE" />
   <input type="image" src="../../style/images/ok.gif" alt="OK" border="0"  />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Action</th>
  <th class="header" colspan="2">Status</th>
  <th class="navigate">View</th>
</tr>
<%
    Module mmconfig=ContextProvider.getDefaultCloudContext().getModule("config");
    if (mmconfig!=null) {
        String check=mmconfig.getInfo("CHECK-applications-"+app);
%>
<tr>
 <td class="data">XML-check</td>
 <td class="data" colspan="2"><%=check%></td>
 <td class="linkdata" >
  <form action="<mm:url page="../config/details.jsp" />" method="POST" target="_xml">
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="applications" />
    <input type="hidden" name="target" value="<%=app%>" />
    <input type="image" src="../../style/images/search.gif" alt="view" border="0"  />
  </form>
 </td>
</tr>
<% } %>

<tr>
 <td class="data">Application Tool</td>
 <td class="data" colspan="2">
     Warning: This will only work if you run MMBase on the same machine as your display unit or have redirected it.<br />
     If this is not the case, use the AppTool as an application.
 </td>
 <td class="linkdata">
  <form action="<mm:url page="result.jsp" />" method="POST">
    <input type="hidden" name="cmd" value="APPTOOL" />
    <input type="hidden" name="application" value="<%=app%>" />
    <input type="hidden" name="APPTOOL" value="<%=app%>" />
    <input type="image" src="../../style/images/ok.gif" alt="OK" border="0"  />
  </form>
 </td>
</tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../applications.jsp" />"><img src="../../style/images/back.gif" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Application Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
