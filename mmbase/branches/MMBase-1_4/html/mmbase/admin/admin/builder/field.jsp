<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<% String builder = request.getParameter("builder");
   String field = request.getParameter("field");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%>, Field <%=field%></title>
<link rel="stylesheet" type="text/css" href="../../css/mmbase.css" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<% String cmd = request.getParameter("cmd");
   Module mmAdmin=LocalContext.getCloudContext().getModule("mmadmin");
   String value = request.getParameter("value");
   String country = request.getParameter("country");
   String msg="";
   if (cmd!=null && value!=null) {
        Hashtable params=new Hashtable();
        params.put("VALUE",value);
        params.put("BUILDER",builder);
        if (country!=null) params.put("COUNTRY",country);
        params.put("FIELDNAME",field);
        mmAdmin.process(cmd,builder,params,request,response);
//        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
   }
%>
<table summary="builder field properties" width="93%" cellspacing="1" cellpadding="3">
<tr align="left">
 <th class="header" colspan="4">Description of <%=builder%> field <%=field%></th>
</tr>
<tr>
 <td class="multidata" colspan="4"><%=msg%>&nbsp;</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
<th class="header">Property</th>
  <th class="header">Value</th>
  <th class="header">Explain</th>
  <th class="header">Change</th>
</tr>
<tr>
  <td class="data">Name</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbname",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbname">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbname&name=Name"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">Type</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbmmbasetype",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbmmbasetype">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbmmbasetype&name=Type"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">State</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbstate",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbstate">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbstate&name=State"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">Required</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbnotnull",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbnotnull">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbnotnull&name=Required"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">Unique/Key</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbkey",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbkey">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbkey&name=Unique/Key"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbsize",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#dbsize">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=dbsize&name=Size"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Editor Property</th>
  <th class="header">Value</th>
  <th class="header">Explain</th>
  <th class="header">Change</th>
</tr>

<tr>
  <td class="data">Input</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorinput",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#editorinput">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=editorinput&name=Input"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">List</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorlist",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#editorlist">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=editorlist&name=List"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<tr>
  <td class="data">Search</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorsearch",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#editorsearch">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=editorsearch&name=Search"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">GUI Property</th>
  <th class="header">Value</th>
  <th class="header">Explain</th>
  <th class="header">Change</th>
</tr>
<tr>
  <td class="data">GUI&nbsp;Type</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-guitype",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#guitype">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=guitype&name=GUI&nbsp;Type"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>

<%
    NodeList names=mmAdmin.getList("ISOGUINAMES-"+builder+"-"+field,null,request,response);
    for (int i=0; i<names.size(); i++) {
        Node name=names.getNode(i);
%>

<tr>
  <td class="data">Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%></td>
  <td class="data"><%=name.getStringValue("item2")%></td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#guiname">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=guiname&name=Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%>&country=<%=name.getStringValue("item2")%>"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>
<% } %>

<tr>
  <td class="data">New&nbsp;Field&nbsp;Name</td>
  <td class="data">&nbsp;</td>
  <td class="linkdata"><a href="/mmdocs/config/builders.html#newguiname">explain</a></td>
  <td class="navigate">
    <a href="setfieldproperty.jsp?builder=<%=builder%>&field=<%=field%>&cmd=newguiname&name=New&nbsp;Field&nbsp;Name"
    ><img src="../../images/change.gif" alt="change" border="0" align="right" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Remove Field</th>
  <th class="header">Check</th>
  <th class="header" colspan="2">Confirm</th>
</tr>

<form action="actions.jsp" method="POST">
<tr>
  <td class="data">Remove <%=field%></td>
<td class="data">
<select name="confirm">
<option selected="selected">No</option>
<option>Yes</option>
</select>
</td>
<td class="linkdata" colspan="2">
    <input type="hidden" name="builder" value="<%=builder%>" />
    <input type="hidden" name="field" value="<%=field%>" />
    <input type="hidden" name="cmd" value="BUILDER-REMOVEFIELD" />
    <input type="submit" value="Remove Now" />
</td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="actions.jsp?builder=<%=builder%>"><img src="../../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data" colspan="3">Return to Builder Administration</td>
</tr>
</table>
</body></html>
</mm:cloud>
