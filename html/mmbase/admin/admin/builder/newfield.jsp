<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*,java.util.*,java.io.*,org.mmbase.util.*,java.net.*,org.mmbase.datatypes.*" %>
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
<script type="text/javascript">
  function getDataTypes() { 
    var datatypes = document.getElementsByTagName("select");
    for (var i = 0; i < datatypes.length; i++) {
    if (datatypes[i].className == "datatype") {
      datatypes[i].name = "datatype_off";
      datatypes[i].style.display = "none";
      }
    }
    var descriptions = document.getElementsByTagName("span");
    for (var i = 0; i < descriptions.length; i++) {
    if (descriptions[i].className == "description") {
      descriptions[i].style.display = "none";
      }
    }
    var selectedDataTypeId = document.getElementById("mmbasetype").value;
    document.getElementById("description_" + selectedDataTypeId).style.display = "block";
    var selectedDataType = document.getElementById("datatype_" + selectedDataTypeId);
    selectedDataType.name = "datatype";
    selectedDataType.style.display = "block";
    selectedSpec = selectedDataType.value;
    if (selectedSpec != "") {
      document.getElementById("description_" + selectedSpec).style.display = "block";
    }


}
</script>
<body class="basic" onLoad="getDataTypes()">

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
      <select name="mmbasetype" id="mmbasetype" onChange="getDataTypes()">
        <%
        Iterator i = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
        while (i.hasNext()) {
          DataType root = (DataType) i.next();
          %>
          <option value="<%=root.getName()%>"><%=root.getLocalizedGUIName().get(Locale.US)%></option>
          <%}%>
      </select>
        <%
         i = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
        while (i.hasNext()) {
          DataType root = (DataType) i.next();
          %>
          <span style="display: none;" class="description" id="description_<%=root.getName()%>">
            <%=root.getLocalizedDescription().get(Locale.US)%>
           </span>
          <%}%>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_type" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>

<% String property; // strange implemetnation%> 
<!--
<tr>
    <td class="data">GUI Type</td>
    <td class="data">
    <% property="guitype"; %>
    <%@include file="properties/guitype.jsp" %>
    </td>
    <td class="navigate"><a href="<mm:url page="/mmdocs/administrators/builders.html#field_guitype" /> " target="_blank"><img src="<mm:url page="/mmbase/style/images/search.gif" />" alt="explain" border="0" /></a></td>
</tr>
-->

<tr>
  <td class="data">Data Type</td>
    <td class="data">
      <% Iterator rootIterator = org.mmbase.datatypes.DataTypes.getSystemCollector().getRoots().iterator();
         while(rootIterator.hasNext()) {
           DataType rootType = (DataType) rootIterator.next();
           %>      
           <select onChange="getDataTypes()" class="datatype" style="display: none;" name="datatype_off" id="datatype_<%=rootType.getName()%>">
           <option value="">--</option>
           <% Iterator j = org.mmbase.datatypes.DataTypes.getSystemCollector().getAllSpecializations(rootType.getName());
           while (j.hasNext()) {
              DataType dataType = (DataType) j.next();
              %>
              <option value="<%=dataType.getName()%>"><%=dataType.getLocalizedGUIName().get(Locale.US)%></option>
              <%}%>
          </select>
           <%  j = org.mmbase.datatypes.DataTypes.getSystemCollector().getAllSpecializations(rootType.getName());
           while (j.hasNext()) {
              DataType dataType = (DataType) j.next();
              %>
              <span class="description" style="display: none;" id="description_<%=dataType.getName()%>">
                <%=dataType.getLocalizedDescription().get(Locale.US)%>
              </span>
          <%}
          }%>
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
