<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase">
<% String module = request.getParameter("module"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Module <%=module%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="module actions" width="93%" cellspacing="1" cellpadding="3">
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");

   String cmd = request.getParameter("cmd");
   String msg="";
   if (cmd!=null) {
    try {
        Hashtable params=new Hashtable();
        params.put("MODULE",module);
        if (cmd.equals("MODULE-SETPROPERTY")) {
            String property=request.getParameter("property").trim();
            if (property.length()==0) {
                throw new Exception("Property name should be specified");
            }
            params.put("PROPERTYNAME",request.getParameter("property"));
            params.put("VALUE",request.getParameter("value"));
        }
        mmAdmin.process(cmd,module,params,request,response);
//        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e.getMessage()+"</p>";
    }
   }
%>
<tr align="left">
 <th class="header" colspan="5">Description of <%=module%></td>
</tr>
<tr>
 <td class="multidata" colspan="5">
        <p><%=mmAdmin.getInfo("MODULEDESCRIPTION-"+module,request,response)%></p>
        <%=msg%>&nbsp;
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Setting</td>
  <th class="header" colspan="3">Value</td>
  <th class="header" >Change</td>
</tr>
<tr>
 <td class="data">Classfile</td>
 <td class="data" colspan="3"><%=mmAdmin.getInfo("MODULECLASSFILE-"+module,request,response)%></td>
 <td class="linkdata" >
    Not Available
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Property</td>
  <th class="header" colspan="3">Value</td>
  <th class="header">Change</td>
</tr>
<%
    NodeList props=mmAdmin.getList("MODULEPROPERTIES-"+module,null,request,response);
    for (int i=0; i<props.size(); i++) {
        Node prop=props.getNode(i);
%>
<tr>
 <td class="data"><%=prop.getStringValue("item1")%></td>
 <td class="data" colspan="3"><%=prop.getStringValue("item2")%>&nbsp;</td>
 <td class="navigate">
    <a href="setproperty.jsp?module=<%=module%>&property=<%=prop.getStringValue("item1")%>"><img src="../../images/pijl.gif" alt="change" border="0" align="right" /></a>
</td>
</tr>

<%  } %>

<tr>
 <td class="data">add new</td>
 <td class="data" colspan="3">&nbsp;</td>
 <td class="navigate">
    <a href="newproperty.jsp?module=<%=module%>"><img src="../../images/pijl.gif" alt="add" border="0" align="right" /></a>
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="result.jsp" method="POST">
<tr align="left">
<th class="header">Action</td>
  <th class="header" colspan="3">Path</td>
  <th class="header">Confirm</td>
</tr>
<tr>
 <td class="data">Save</td>
 <td class="data" colspan="3"><input name="path" value="/tmp/<%=module%>.xml" /></td>
 <td class="linkdata" >
   <input type="hidden" name="module" value="<%=module%>" />
   <input type="hidden" name="cmd" value="MODULESAVE" />
   <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<%
    Module config=LocalContext.getCloudContext().getModule("config");
    if (config!=null) {
        String check=config.getInfo("CHECK-modules-"+module);
%>
<form action="../config/details.jsp" method="POST" target="_xml">
<tr align="left">
<th class="header">Action</td>
  <th class="header" colspan="3">Status</td>
  <th class="header" >View</td>
</tr>
<tr>
 <td class="data">XML-check</td>
 <td class="data" colspan="3"><%=check%></td>
 <td class="linkdata" >
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="modules" />
    <input type="hidden" name="target" value="<%=module%>" />
    <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<% } %>

<tr>
<td class="navigate"><a href="../modules.jsp"><img src="../../images/pijl2.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to Module Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
