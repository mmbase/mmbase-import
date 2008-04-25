<%@page   contentType="text/html;charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<% String builder = request.getParameter("builder");
   String field = request.getParameter("field");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%>, Field <%=field%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >
<% String cmd = request.getParameter("cmd");
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
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
<table summary="builder field properties">
<tr>
 <th class="header" colspan="4">Description of <%=builder%> field <%=field%></th>
</tr>
<tr>
 <td class="multidata" colspan="4"><%=msg%>&nbsp;</td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Property</th>
  <th class="header">Value</th>
  <th class="navigate">explain</th>
  <th class="navigate">Change</th>
</tr>
<tr>
  <td class="data">Name</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbname",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_name" />" target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=dbname&name=Name"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Type</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbmmbasetype",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_type" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="changetype.jsp?builder="+builder+"&field="+field%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Data Type</td>
  <td class="data"><%=cloud.getNodeManager(builder).getField(field).getDataType().toXml()%></td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#data_type" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="changetype.jsp?builder="+builder+"&field="+field%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">State</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbstate",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_state" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=dbstate&name=State"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Required</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbnotnull",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_notnull" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=dbnotnull&name=Required"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Unique/Key</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbkey",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_key" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=dbkey&name=Unique/Key"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Size</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-dbsize",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_size" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=dbsize&name=Size"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">Editor Property</th>
  <th class="header">Value</th>
  <th class="navigate">explain</th>
  <th class="navigate">Change</th>
</tr>

<tr>
  <td class="data">Input</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorinput",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_input" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=editorinput&name=Input"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">List</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorlist",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_list" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=editorlist&name=List"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<tr>
  <td class="data">Search</td>
  <td class="data"><%=mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-editorsearch",request,response)%>&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_search" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=editorsearch&name=Search"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">GUI Property</th>
  <th class="header">Value</th>
  <th class="navigate">explain</th>
  <th class="navigate">Change</th>
</tr>

<%
   java.util.Map params = new java.util.Hashtable();
   params.put("CLOUD", cloud);
    NodeList names=mmAdmin.getList("ISOGUINAMES-"+builder+"-"+field, params,request,response);
    for (int i=0; i<names.size(); i++) {
        Node name=names.getNode(i);
%>

<tr>
  <td class="data">Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%></td>
  <td class="data"><%=name.getStringValue("item2")%></td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_guiname" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=guiname&name=Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;"+name.getStringValue("item1")+"&country="+name.getStringValue("item1")%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<% } %>

<tr>
  <td class="data">New&nbsp;Field&nbsp;Name</td>
  <td class="data">&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_guiname" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=newguiname&name=New&nbsp;Field&nbsp;Name"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">GUI Descriptions</th>
  <th class="header">Value</th>
  <th class="navigate">explain</th>
  <th class="navigate">Change</th>
</tr>

<%
    params.clear();
    params.put("CLOUD", cloud);
    names=mmAdmin.getList("ISODESCRIPTIONS-"+builder+"-"+field, params,request,response);
    for (int i=0; i<names.size(); i++) {
        Node name=names.getNode(i);
%>

<tr>
  <td class="data">Description&nbsp;for&nbsp;ISO&nbsp;639&nbsp;<%=name.getStringValue("item1")%></td>
  <td class="data"><%=name.getStringValue("item2")%></td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_description" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=description&name=Field&nbsp;Name&nbsp;for&nbsp;ISO&nbsp;639&nbsp;"+name.getStringValue("item1")+"&country="+name.getStringValue("item1")%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>
<% } %>

<tr>
  <td class="data">New&nbsp;Description</td>
  <td class="data">&nbsp;</td>
  <td class="linkdata"><a href="<mm:url page="http://www.mmbase.org/mmdocs18/informationanalysts/builders.html#field_description" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
  <td class="navigate">
    <a href="<mm:url page="<%="setfieldproperty.jsp?builder="+builder+"&field="+field+"&cmd=newdescription&name=New&nbsp;Field&nbsp;Name"%>" />"
    ><img src="<mm:url page="/mmbase/style/images/change.gif" />" alt="change" border="0" /></a>
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<form action="<mm:url page="actions.jsp" />" method="POST">
<tr>
  <th class="header">Remove Field</th>
  <th class="header">Are you sure?</th>
  <th class="navigate" colspan="2">Confirm</th>
</tr>
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
    <input type="image" src="<mm:url page="/mmbase/style/images/delete.gif" />" alt="Remove Now" border="0"  />
</td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="actions.jsp?builder="+builder%>" />"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Builder Administration</td>
</tr>
</table>
</body></html>
</mm:cloud>
