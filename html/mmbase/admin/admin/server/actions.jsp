<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<%  String server=request.getParameter("server"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Server <%=server%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="server actions" width="93%" cellspacing="1" cellpadding="3">
<tr align="left">
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
<form action="result.jsp" method="POST">
<tr align="left">
  <th class="header">Action</th>
  <th class="header">Confirm</th>
</tr>
<tr>
  <td class="data">Restart Server</td>
  <td class="linkdata">
   <input type="hidden" name="server" value="<%=server%>" />
   <input type="hidden" name="cmd" value="SERVERRESTART" />
   <input type="submit" value="YES" />
  </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../servers.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data">Return to Server Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
