<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builders</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="builders" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
<th class="header" colspan="5">Builder Overview
</td>
</tr>
<tr>
  <td class="multidata" colspan="5">
  <p>This overview lists all known builders.<br />
     The first list contains all builders that are currently 'active' (accessible through MMBase).<br />
     The second list (if available) lists all builders for which the definition is known, but which are currently inactive
     (and thus inaccessible).
  </p>
</tr>
<tr><td>&nbsp;</td></tr>
<tr align="left">
  <th class="header">Name</td>
  <th class="header">Version</td>
  <th class="header">Installed</td>
  <th class="header">Maintainer</td>
  <th class="header">&nbsp;</td>
</tr>


<%
    Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
    NodeList builders=mmAdmin.getList("BUILDERS",null,request,response);
    for (int i=0; i<builders.size(); i++) {
        Node builder=builders.getNode(i);
        if (!builder.getStringValue("item3").equals("no")) {
%>
<tr>
  <td class="data"><%=builder.getStringValue("item1")%></td>
  <td class="data"><%=builder.getStringValue("item2")%></td>
  <td class="data"><%=builder.getStringValue("item3")%></td>
  <td class="data"><%=builder.getStringValue("item4")%></td>
  <td class="navigate">
    <a href="builder/actions.jsp?builder=<%=builder.getStringValue("item1")%>"><img src="../images/pijl.gif" alt="next" border="0" align="right"></a>
  </td>
</tr>
<%      }
    }
%>
<tr><td>&nbsp;</td></tr>
<%
    for (int i=0; i<builders.size(); i++) {
        Node builder=builders.getNode(i);
        if (builder.getStringValue("item3").equals("no")) {
%>
<tr>
  <td class="data"><%=builder.getStringValue("item1")%></td>
  <td class="data"><%=builder.getStringValue("item2")%></td>
  <td class="data"><%=builder.getStringValue("item3")%></td>
  <td class="data"><%=builder.getStringValue("item4")%></td>
  <td class="navigate">
    <a href="builder/actions.jsp?builder=<%=builder.getStringValue("item1")%>"><img src="../images/pijl.gif" alt="next" border="0" align="right"></a>
  </td>
</tr>
<%      }
    }
%>
<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/pijl2.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
