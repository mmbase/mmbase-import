<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" jspvar="cloud">

<mm:import externid="editid" required="true" />

<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" href="css/editors.css">

<script language="javascript">
<!--
function openListNodes(fullField1,fullField2) {
	var fullConstraints = document.searchform.constraints.value;
	var zoek = document.searchform.nodedesc.value.toUpperCase();
	var constraints = '';
	
	if (fullConstraints != '') { 
		constraints = fullConstraints;
	}
	if (zoek != '') {
      	if (fullField1 != '') {
			if (constraints != '') {
				constraints += " and ";
			}
		
	   		constraints += "( UPPER(";
			constraints += fullField1;
			constraints += ") LIKE '%" + zoek + "%'"; 
			if (fullField2 != '') { 
   				constraints += " or UPPER(";
				constraints += fullField2;
				constraints += ") LIKE '%" + zoek + "%'"; 
       		}
		} 
		constraints += ")";
	}
	document.searchform.constraints.value = constraints;
	return false;
}
-->
</script>
</head>
<body bgcolor="#EFEFEF" text="#000000" link="#000000" alink="#000000" vlink="#CC0000" topmargin="2" rightmargin="3" leftmargin="3">

<mm:node number="$editid">

<mm:field name="fields" jspvar="fields" vartype="string">
<% 
// get fields for constraint (max only two)
int seperator = -1;
String fullField1 = "";
String fullField2 = "";

seperator = fields.indexOf(",");
if(seperator !=-1) {
	fullField1 = fields.substring(0,seperator); 
	fields = fields.substring(seperator+1);
	seperator =  fields.indexOf(",");
	if(seperator != -1) {
		fullField2 = fields.substring(0,seperator); 
	} else {
		fullField2 = fields;
	}
} else {
	fullField1 = fields;
	fields = "";
}
%>
<table cellpadding="0" cellspacing="0" border="0" align="center">
<tr><td valign="bottom"><div align="center">
		<mm:field name="edittranslate.title" />
		</div>
	</td></tr>
	<tr><td valign="top">
	<table cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td>Search in <%= fullField1 %> <% if(!fullField2.equals("")) { %> and <%= fullField2 %> <% } %>&nbsp;&nbsp;</td>
		<td>

			<mm:import id="jsppage"><mm:field name="jsppage"/></mm:import>

			<form target="wizard" name="searchform"
				  action="<mm:url page="$jsppage"/>"
				  onSubmit="return openListNodes('<%= fullField1 %>','<%= fullField2 %>');">

				<input type="hidden" name="language" value="en">
				<input type="hidden" name="builder" value="<mm:field name="builder"/>">
				<input type="hidden" name="orderby" value="<mm:field name="orderby"/>">
				<input type="hidden" name="directions" value="<mm:field name="directions"/>">
				<input type="hidden" name="translationfields" value="<mm:field name="translationfields"/>">
				<input type="hidden" name="translationinput" value="<mm:field name="translationinput"/>">
				<input type="hidden" name="show" value="<mm:field name="display"/>">
				<input type="hidden" name="fields" value="<mm:field name="fields"/>">
				<input type="hidden" name="constraints" value="<mm:field name="constraints"/>">
				<input type="hidden" name="editid" value="<mm:write referid="editid"/>">
				
				<input type="text" name="nodedesc" value=""  style="width:200px;text-align:left;" />
			</form>				
		</td>
	</tr>
	<tr>
	<td>&nbsp;</td>

	<td class="right">
	<span onClick="javascript:document.searchform.submit();">     
           <img title="Search" height="20" width="20" border="0" src="media/search.gif"></span></td>
	</tr>
	</table>
</td></tr></table>
</mm:field>

</mm:node>
</body>
</html>
</mm:cloud>