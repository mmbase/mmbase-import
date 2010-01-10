<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@page import="org.mmbase.bridge.*"
%><mm:content expires="0">
<mm:cloud rank="administrator">
<% String app = request.getParameter("application"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Application <%=app%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
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
 <td class="data" >Version: <%=mmAdmin.getInfo("VERSION-"+app, request, response)%> </td>
 <td class="linkdata" >
  <form action="<mm:url page="result.jsp" />" method="POST">
   <input type="hidden" name="application" value="<%=app%>" />
   <input type="hidden" name="cmd" value="LOAD" />
   <input type="image" src="<mm:url page="/mmbase/style/images/ok.gif" />" alt="OK" border="0"  />
  </form>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="<mm:url page="result.jsp" />" method="POST">
<tr>
<th class="header">Action</th>
  <th class="header">Path</th>
  <!--
  <th class="header">Goal</th>
  -->
  <th class="navigate">Confirm</th>
</tr>
<tr>
 <td class="data" >Save <%=app%></td>
 <td class="data" ><input type="text" name="path" value="/tmp" size="80" /></td>
 <input type="hidden" name="goal" value="backup" />
<!--
 <td class="data" ><select name="goal">
   <option selected="selected">backup</option>
 </select>
 </td>
 -->
 <td class="linkdata" >
   <input type="hidden" name="application" value="<%=app%>" />
   <input type="hidden" name="cmd" value="SAVE" />
   <input type="image" src="<mm:url page="/mmbase/style/images/ok.gif" />" alt="OK" border="0"  />
 </td>
</tr>
</form>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../applications.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Application Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
</mm:content>