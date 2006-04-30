<%@page   contentType="text/html;charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<%@include file="../../settings.jsp" %>
<mm:content expires="0">
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String builder = request.getParameter("builder");
   String field = request.getParameter("field");
   String name=request.getParameter("name");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%>, <%=name%> of Field <%=field%></title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
</head>
<body class="basic" >
<% Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
   String cmd=request.getParameter("cmd");
   String country=request.getParameter("country");
   String value=null;
   if (cmd.equals("guiname")) {
       value= mmAdmin.getInfo("GETGUINAMEVALUE-"+builder+"-"+field+"-"+country,request,response);
   } else if(cmd.equals("description")) {
       value= mmAdmin.getInfo("GETDESCRIPTION-"+builder+"-"+field+"-"+country,request,response);
   } else {
       value= mmAdmin.getInfo("GETBUILDERFIELD-"+builder+"-"+field+"-"+cmd,request,response);
   }
   String property="value";
%>
<table summary="builder field property data">
<form action="<mm:url page="field.jsp" />" method="POST">
<tr>
  <th class="header">Property</th>
  <% if (cmd.equals("newguiname") || cmd.equals("newdescription")) { %>
    <th class="header">Country Code / Value</th>
  <% } else { %>
    <th class="header">Value</th>
  <% } %>
  <th class="navigate">Change</th>
</tr>
<tr>
  <td class="data"><%=name%></td>
 <td class="data">
  <% if (cmd.equals("dbmmbasetype")) {%>

  <% } else if (cmd.startsWith("editor")) {%>
<%@include file="properties/editorpos.jsp" %>
  <% } else if (cmd.equals("guitype")) {%>

  <% } else if (cmd.equals("dbstate")) {%>
<%@include file="properties/dbstate.jsp" %>
  <% } else if (cmd.equals("dbkey") || cmd.equals("dbnotnull")) {%>
<%@include file="properties/truefalse.jsp" %>
  <% } else if (cmd.equals("newguiname")) {
        cmd="guiname";
        value=null;
  %>
<% property="country"; %>
<%@include file="properties/iso639.jsp" %> <!--hff iso639 are not countries, but languages... -->
/ <input type="text" name="value" value="<%=value%>" />
  <% } else if (cmd.equals("newdescription")) {
        cmd="description";
        value=null;
  %>
<% property="country"; %>
<%@include file="properties/iso639.jsp" %>
/ <input type="text" name="value" value="<%=value%>" />
  <% } else { %>
<input type="text" name="value" value="<%=value%>" />
  <% } %>

</td>
<td class="linkdata">
    <input type="hidden" name="builder" value="<%=builder%>" />
    <input type="hidden" name="field" value="<%=field%>" />
<% if (country!=null) { %>
    <input type="hidden" name="country" value="<%=country%>" />
<% } %>
    <input type="hidden" name="cmd" value="BUILDER-SET<%=cmd.toUpperCase()%>" />
    <input type="image" src="<mm:url page="/mmbase/style/images/change.gif" />" alt="Change" border="0"  />
</td>
</tr>
</form>

<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="<%="field.jsp?builder="+builder+"&field="+field%>"/>"><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" border="0" /></td>
<td class="data" colspan="3">Return to Builder Field Administration</td>
</tr>
</table>
</body></html>
</mm:cloud>
</mm:content>