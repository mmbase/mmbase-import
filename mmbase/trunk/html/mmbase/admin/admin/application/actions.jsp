<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" logon="admin">
<% String application = request.getParameter("application"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Application <%=application%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="application actions" width="93%" cellspacing="1" cellpadding="3">
<%
    Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
%>
<tr align="left">
 <th class="header" colspan="4">Description of <%=application%></th>
</tr>
<tr>
 <td class="multidata" colspan="4">
        <p><%=mmAdmin.getInfo("DESCRIPTION-"+application,request,response)%></p>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="result.jsp" method="POST">
<tr align="left">
<th class="header" colspan="2">Action</th>
  <th class="header" >&nbsp;</th>
  <th class="header">Confirm</th>
</tr>
<tr>
 <td class="data" colspan="2">Install <%=application%></td>
 <td class="data" >Version: <%=mmAdmin.getInfo("VERSION-"+application,request,response)%> </td>
 <td class="linkdata" >
   <input type="hidden" name="application" value="<%=application%>" />
   <input type="hidden" name="cmd" value="LOAD" />
   <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<form action="result.jsp" method="POST">
<tr align="left">
<th class="header">Action</th>
  <th class="header">Path</th>
  <th class="header">Goal</th>
  <th class="header">Confirm</th>
</tr>
<tr>
 <td class="data" >Save <%=application%></td>
 <td class="data" ><input type="text" name="path" value="/tmp"/></td>
 <td class="data" ><select name="goal">
        <option selected="selected">backup</option>
    </select>
 </td>
 <td class="linkdata" >
   <input type="hidden" name="application" value="<%=application%>" />
   <input type="hidden" name="cmd" value="SAVE" />
   <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Action</th>
  <th class="header" colspan="2">Status</th>
  <th class="header">View</th>
</tr>
<tr>
<%
    Module config=LocalContext.getCloudContext().getModule("config");
    if (config!=null) {
        String check=config.getInfo("CHECK-applications-"+application);
%>
<form action="../config/details.jsp" method="POST" target="_xml">
 <td class="data">XML-check</td>
 <td class="data" colspan="2"><%=check%></td>
 <td class="linkdata" >
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="applications" />
    <input type="hidden" name="target" value="<%=application%>" />
    <input type="submit" value="YES" />
 </td>
</tr>
</form>
<% } %>

<form action="result.jsp" method="POST">
<tr>
 <td class="data">Application Tool</td>
 <td class="data" colspan="2">
     <p>Warning this will only work if you run MMBase on the same
     machine as your display unit or have redirected it.<br />
     If this is not the case, use the AppTool as an application.
     </p>
 </td>
 <td class="linkdata">
    <input type="hidden" name="cmd" value="APPTOOL" />
    <input type="hidden" name="application" value="<%=application%>" />
    <input type="hidden" name="APPTOOL" value="<%=application%>" />
    <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr>
<td class="navigate"><a href="../applications.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Application Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
