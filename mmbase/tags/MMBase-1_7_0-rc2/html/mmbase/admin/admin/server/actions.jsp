<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<%  String server=request.getParameter("server"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Server <%=server%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="server actions">
<tr>
  <th class="header" colspan="2" >Administrate Server: <%=server%></th>
</tr>
<tr>
  <td class="multidata" colspan="2">
   <p>
      JVM memory size : <%=(Runtime.getRuntime().totalMemory()*10/1048576)/10.0%> Mb (<%=(Runtime.getRuntime().totalMemory()*10/1024)/10.0%> Kb)<br />
      JVM free memory : <%=(Runtime.getRuntime().freeMemory()*10/1048576)/10.0%> Mb (<%=(Runtime.getRuntime().freeMemory()*10/1024)/10.0%> Kb)
   </p>
  </td>
</tr>
<tr><td><br /></td></tr>

<tr>
  <th class="header">Action</th>
  <th class="navigate">Confirm</th>
</tr>
<tr>
  <td class="data">Stop/restart Server <br />
   Note: this method actually stops the JVM in which the application server runs.<br />
   This means that the server will only restart if you have a script running that starts the server again
   when it goes down (a simple looping script will work).<br />
   If you do not have such a script, or do not wish to use restart through this page,
   we suggest you remove this option from the admin pages.
  </td>
  <td class="linkdata">
<form action="<mm:url page="result.jsp" />" method="POST">
   <input type="hidden" name="server" value="<%=server%>" />
   <input type="hidden" name="cmd" value="SERVERRESTART" />
   <input type="submit" value="YES, STOP THE SERVER" />
</form>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../servers.jsp" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data">Return&nbsp;to&nbsp;Server&nbsp;Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
