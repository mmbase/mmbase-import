<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<% String builder = request.getParameter("builder"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<table summary="builder actions" width="93%" cellspacing="1" cellpadding="3">
<%
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");

   String cmd = request.getParameter("cmd");
   String msg="";
   if (cmd!=null) {
    try {
        Hashtable params=new Hashtable();
        params.put("BUILDER",builder);
        if (cmd.equals("BUILDER-REMOVEFIELD")) {
            params.put("FIELDNAME",request.getParameter("field"));
            params.put("SURE",request.getParameter("confirm"));
        } else if (cmd.equals("BUILDER-ADDFIELD")) {
            params.put("dbname",request.getParameter("dbname"));
            params.put("mmbasetype",request.getParameter("mmbasetype"));
            params.put("guitype",request.getParameter("guitype"));
            params.put("dbstate",request.getParameter("dbstate"));
            params.put("dbnotnull",request.getParameter("dbnotnull"));
            params.put("dbkey",request.getParameter("dbkey"));
            params.put("dbsize",request.getParameter("dbsize"));
        }
        mmAdmin.process(cmd,builder,params,request,response);
//        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (Exception e ) {
        msg="<p> Error: "+e.getMessage()+"</p>";
    }
   }
%>
<tr align="left">
 <th class="header" colspan="5">Description of <%=builder%></th>
</tr>
<tr>
 <td class="multidata" colspan="5">
        <p><%=mmAdmin.getInfo("BUILDERDESCRIPTION-"+builder,request,response)%></p>
        <%=msg%>&nbsp;
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Setting</th>
  <th class="header" colspan="2">Value</th>
  <th class="header" colspan="2">Change</th>
</tr>
<tr>
 <td class="data">Classfile</td>
 <td class="data" colspan="2"><%=mmAdmin.getInfo("BUILDERCLASSFILE-"+builder,request,response)%></td>
 <td class="linkdata" colspan="2">
    Not Available
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Field</th>
  <th class="header">Name</th>
  <th class="header">Type</th>
  <th class="header">Size</th>
  <th class="header">More</th>
</tr>
<%
    NodeList fields=mmAdmin.getList("FIELDS-"+builder,null,request,response);
    for (int i=0; i<fields.size(); i++) {
        Node field=fields.getNode(i);
%>
<tr>
 <td class="data"><%=field.getStringValue("item1")%></td>
 <td class="data"><%=field.getStringValue("item2")%></td>
 <td class="data"><%=field.getStringValue("item3")%></td>
 <td class="data"><%=field.getStringValue("item4")%></td>
 <td class="navigate">
    <a href="field.jsp?builder=<%=builder%>&field=<%=field.getStringValue("item2")%>"><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
</td>
</tr>

<%  } %>
<tr>
 <td class="data">add new</td>
 <td class="data">&nbsp;</td>
 <td class="data">&nbsp;</td>
 <td class="data">&nbsp;</td>
 <td class="navigate">
    <a href="newfield.jsp?builder=<%=builder%>"><img src="../../images/next.gif" alt="add new" border="0" align="right" /></a>
</td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="result.jsp" method="POST">
<tr align="left">
<th class="header">Action</th>
  <th class="header" colspan="2">Path</th>
  <th class="header" colspan="2">Confirm</th>
</tr>
<tr>
 <td class="data">Save</td>
 <td class="data" colspan="2"><input name="path" value="/tmp/<%=builder%>.xml" /></td>
 <td class="linkdata" colspan="2">
   <input type="hidden" name="builder" value="<%=builder%>" />
   <input type="hidden" name="cmd" value="BUILDERSAVE" />
   <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<%
    Module mmconfig=LocalContext.getCloudContext().getModule("config");
    if (mmconfig!=null) {
        String check=mmconfig.getInfo("CHECK-builders-"+builder);
%>
<form action="../config/details.jsp" method="POST" target="_xml">
<tr align="left">
<th class="header">Action</th>
  <th class="header" colspan="2">Status</th>
  <th class="header" colspan="2">View</th>
</tr>
<tr>
 <td class="data">XML-check</td>
 <td class="data" colspan="2"><%=check%></td>
 <td class="linkdata" colspan="2">
<%    if (check.equals("Checked ok")) { %>
        <input type="hidden" name="todo" value="show" />
<%  } else { %>
        <input type="hidden" name="todo" value="annotate" />
<%  } %>
    <input type="hidden" name="config" value="builders" />
    <input type="hidden" name="target" value="<%=builder%>" />
    <input type="submit" value="YES" />
 </td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<% } %>

<tr>
<td class="navigate"><a href="../builders.jsp"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="4">Return to Builder Overview</td>
</tr>
</table>
</body></html>
</mm:cloud>
