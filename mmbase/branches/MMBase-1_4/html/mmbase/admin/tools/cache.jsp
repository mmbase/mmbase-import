<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Cache Monitor</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="email test" width="93%" cellspacing="1" cellpadding="3" border="0">

<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
%>

<tr align="left">
  <th class="header" colspan="2">Cache Monitor - v1.0</th>
</tr>
<tr>
  <td class="multidata" colspan="2"><p>This tools hows the performance of the various MMBase caches.</p></td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Multi Level Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%=mmAdmin.getInfo("MULTILEVELCACHEREQUESTS",request,response)%></td>
</tr>
<tr>
  <td class="data">Hits</td>
  <td class="data"><%=mmAdmin.getInfo("MULTILEVELCACHEHITS",request,response)%></td>
</tr>
<tr>
  <td class="data">Misses</td>
  <td class="data"><%=mmAdmin.getInfo("MULTILEVELCACHEMISSES",request,response)%></td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%=mmAdmin.getInfo("MULTILEVELCACHESIZE",request,response)%></td>
</tr>
<tr>
  <td class="data">Performance</td>
  <td class="data"><%=mmAdmin.getInfo("MULTILEVELCACHEPERFORMANCE",request,response)%></td>
</tr>
<tr>
  <td class="data">Show first 500 entry's of the cache</td>
  <td class="navigate">
    <a href="cache/multilevelcache.jsp"><img src="../images/next.gif" alt="next" border="0" align="right"></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Node Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%=mmAdmin.getInfo("NODECACHEREQUESTS",request,response)%></td>
</tr>
<tr>
  <td class="data">Hits</td>
  <td class="data"><%=mmAdmin.getInfo("NODECACHEHITS",request,response)%></td>
</tr>
<tr>
  <td class="data">Misses</td>
  <td class="data"><%=mmAdmin.getInfo("NODECACHEMISSES",request,response)%></td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%=mmAdmin.getInfo("NODECACHESIZE",request,response)%></td>
</tr>
<tr>
  <td class="data">Performance</td>
  <td class="data"><%=mmAdmin.getInfo("NODECACHEPERFORMANCE",request,response)%></td>
</tr>
<tr>
  <td class="data">Show first 1000 entry's of the cache</td>
  <td class="navigate">
    <a href="cache/nodecache.jsp"><img src="../images/next.gif" alt="next" border="0" align="right"></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Relation Cache Property</th>
  <th class="header">Value</th>
</tr>
<tr>
  <td class="data">Requests</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEREQUESTS",request,response)%></td>
</tr>
<tr>
  <td class="data">Hits</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEHITS",request,response)%></td>
</tr>
<tr>
  <td class="data">Misses</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEMISSES",request,response)%></td>
</tr>
<tr>
  <td class="data">Performance</td>
  <td class="data"><%=mmAdmin.getInfo("RELATIONCACHEPERFORMANCE",request,response)%></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data">Return to home page</td>
</tr>
</table>

</body></html>
</mm:cloud>
