<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@include file="../../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator">
<% String builder = request.getParameter("builder"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Administrate Builder <%=builder%>, New Field</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
</head>
<body class="basic" >

<% String value=null;
   Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
%>
<table summary="new builder field properties">
<tr>
 <th class="header" colspan="3">Add New Field to Builder <%=builder%></th>
</tr>
<tr>
 <td class="multidata" colspan="3">
 <p>WARNING: this page allows you to add fields to this object.<br />
    Make sure that you have a backup and know what you are doing.<br />
    Some of this might not be tested on your database system.<br />
    Use at your own risk.
 </p>
 </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<th class="header">Field Property</th>
  <th class="header">Value</th>
  <th class="navigate">Explain</th>
</tr>

<form action="<mm:url page="actions.jsp"/>" method="POST">
<tr>
    <td class="data">Name</td>
    <td class="data"><input type="text" name="dbname" value="" /></td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_name" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">Type</td>
    <td class="data">
    <% String property="mmbasetype"; %>
<%@include file="properties/dbmmbasetype.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_type" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">GUI Type</td>
    <td class="data">
    <% property="guitype"; %>
<%@include file="properties/guitype.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_guitype" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">State</td>
    <td class="data">
    <% property="dbstate"; %>
<%@include file="properties/dbstate.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_state" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">Required</td>
    <td class="data">
    <% property="dbnotnull"; %>
<%@include file="properties/truefalse.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_notnull" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">Unique</td>
    <td class="data">
    <% property="dbkey"; %>
<%@include file="properties/truefalse.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_key" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data">Size</td>
    <td class="data"><input type="text" name="dbsize" value="" /></td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_size" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<tr>
    <td class="data" colspan="2">
        <p>Make sure all the settings are valid and what you want before updating the object</p>
    <input type="hidden" name="builder" value="<%=builder%>" />
    <input type="hidden" name="cmd" value="BUILDER-ADDFIELD" />
    </td>
    <td class="linkdata"><input type="submit" value="Add Field" /></td>
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
