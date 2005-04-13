<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
        "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%	userconstraint += " AND editwizards.number='" + editwizardsId + "'"; %>

<%@include file="getParam.jsp" %>

<%! public String strip(String fieldString, String nodetype) {
		String stripped = "";
		String field = "";
		while(!fieldString.equals("")) {
			int sep = fieldString.indexOf(",");
			if(sep>-1) {
				field = fieldString.substring(0,sep);
				fieldString = fieldString.substring(sep+1);
			} else {
				field = fieldString;
				fieldString = "";
			}
			if(field.indexOf(nodetype)>-1) { 
			// field does not contain nodetype => throw field away	
			// else take part from the "."
				field = field.substring(field.indexOf(".")+1);
				if(!stripped.equals("")) stripped += ",";
				stripped += field;
			}
		}
		return stripped;
}
%>

<mm:import id="referrer"><%= new java.io.File(request.getServletPath()) %></mm:import>

<html>
<head>
<title>Select</title>
<link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmmbase.css" />">
<style type="text/css" title="text/css" media="screen">
/* <![CDATA[ */
#windowopen
{
	margin: 0 7% 0 7%;
	padding: 2px 4px 2px 4px;
}
/* ]]> */
</style>
<script language="javascript" type="text/javascript">
<!--
function openListNodes(el,fullField1,minField1,fullField2,minField2,fullPath,minPath,fullConstraints,minConstraints) {
	var href = el.getAttribute("href");
	var action = document.forms[0].action;
	if(action != '') { href = action; }
	var zoek = document.forms[0].elements["nodedesc"].value.toUpperCase();
	var age = document.forms[0].elements["age"].value;
	href += fullPath; 
	if (fullConstraints != '') { 
		href += "&constraints=" + fullConstraints;
	}
	if (zoek != '') { 
      	if (fullField1 != '') { 
		if (fullConstraints != '') { href += "%20and%20"; }
		else { href += "&constraints="; } 
       		href += "%28%20UPPER%28";
		href += fullField1;
		href += "%29%20LIKE%20%27%25" + zoek + "%25%27"; 
		if (fullField2 != '') { 
   			href += "%20or%20UPPER%28";
			href += fullField2;
			href += "%29%20LIKE%20%27%25" + zoek + "%25%27"; 
       		}
		} 
		href += "%29";
	} 
    if (age != '') { 
		href += "&age=" + age; 
	}
    top.wizard.document.location = href; 
    return false; 
} 
-->
</script>
</head>
<body>
<div id="windowopen">
<mm:list path="mmbaseusers,groups,editwizards" max="1" constraints="<%= "editwizards.number='" + editwizardsId + "'" %>">
<mm:field name="editwizards.url" jspvar="editwizards_url" vartype="String" write="false">

<% // get the fields for use in the form
String nodepath = getParam(editwizards_url,"nodepath=","&");
String startnodes = getParam(editwizards_url,"startnodes=","&");
String fields =  getParam(editwizards_url,"fields=","&"); 
String orderby =  getParam(editwizards_url,"orderby=","&"); 
String nodetype = nodepath.substring(nodepath.lastIndexOf(",")+1);
boolean nodepathIsOfLengthTwoOrMore = (nodepath.indexOf(",")>-1);
int seperator = -1;
// create two constraints e.g. news.type='6' and type='6' (assumption only constrains on fields of the node of type nodetype)
String fullConstraints =  getParam(editwizards_url,"constraints=","&"); 
String minConstraints = fullConstraints;
if(nodepathIsOfLengthTwoOrMore) {
	seperator =  fullConstraints.indexOf(nodetype + ".");
	while (seperator != -1) {
		minConstraints = minConstraints.substring(0,seperator) + minConstraints.substring(seperator+nodetype.length()+1);
		seperator =  minConstraints.indexOf(nodetype + ".");
	}
}
// create the two paths for use with a nodepath of one node (minPath) or of two or more nodes (fullPath)
String fullPath = "&fields=" + fields + "&nodepath=" + nodepath;
if(!startnodes.equals("")) { fullPath += "&startnodes=" + startnodes; }
if(!orderby.equals("")) { fullPath += "&orderby=" + orderby; }
String minPath = "";
if(nodepathIsOfLengthTwoOrMore) {
	minPath = "&fields=" + strip(fields,nodetype) + "&nodepath=" + strip(nodepath,nodetype) + "&orderby=" + strip(orderby,nodetype);
} else {
	minPath = fullPath;
}

// get fields for constraint e.g. news.title and news.subtitle
String fullField1 = "";
String fullField2 = "";
String minField1 = "";
String minField2 = "";
seperator =  fields.indexOf(",");
if(seperator !=-1) {
	fullField1 = fields.substring(0,seperator); 
	fields = fields.substring(seperator+1);
	seperator =  fields.indexOf(",");
	if(seperator != -1) {
		fullField2 = fields.substring(0,seperator); 
	} else {
		fullField2 = fields;
	}
	// fullField2 should contain the nodetype e.g. magazines.title should not be incorporated in the search
	if(nodepathIsOfLengthTwoOrMore&&(fullField2.indexOf(nodetype + "." )== -1)) fullField2 = "";
} else {
	fullField1 = fields;
	fields = "";
}
// minfields should be the fullfields without the nodetype e.g. title and subtitle
minField1 = fullField1.substring(fullField1.indexOf(".")+1);
minField2 = fullField2.substring(fullField2.indexOf(".")+1);
%>
<table cellpadding="0" cellspacing="0" border="0">
<tr><td valign="bottom">
		<mm:field name="editwizards.title" />
		<% if(!orderby.equals("")) {
			String orderbyString = orderby;
			if(orderbyString.indexOf("number")>-1) { orderbyString = "leeftijd"; }
			 %> (gesorteerd op <%= orderbyString %>) 
		<% } %>
	</td></tr>
	<tr><td valign="top">
	<form action="<mm:url referids="referrer" page="/mmbase/editwizard/jsp/list.jsp"> 
           			<mm:param name="wizard"><%= getParam(editwizards_url,"wizard=","&") %></mm:param> 
 	   	   		<mm:param name="directions"><%= getParam(editwizards_url,"directions=","&") %></mm:param>
		   	</mm:url>"
	      onsubmit="return openListNodes(this,'<%= fullField1 %>','<%= minField1 %>','<%= fullField2 %>','<%= minField2 %>','<%= fullPath %>','<%= minPath %>', '<%= fullConstraints %>', '<%= minConstraints %>');"
     	>
	<table cellpadding=0 cellspacing=0 border=0>
	<tr>
		<td>Zoek in <%= fullField1 %> <% if(!fullField2.equals("")) { %> en <%= fullField2 %> <% } %>&nbsp;&nbsp;</td>
		<td><input type="text" name="nodedesc" value=""  style="width:200px;text-align:left;" /></td>
	</tr>
	<tr>
		<td>Leeftijd&nbsp;&nbsp;</td>
		<td><% String age = getParam(editwizards_url,"age=","&"); %>
			<select type="option" name="age">
				<option value="1" <% if(age.equals("1")){ %> selected <% } %>>1 dag</option>
				<option value="7" <% if(age.equals("7")){ %> selected <% } %>>1 week</option>
				<option value="31" <% if(age.equals("31")){ %> selected <% } %>>1 maand</option>
				<option value="365" <% if(age.equals("365")){ %> selected <% } %>>1 jaar</option>
				<option value="-1" <% if(age.equals("-1")||age.equals("")){ %> selected <% } %>>alles</option>
			</select> 
		</td>
	</tr>
	<tr>
	<td>
	<a target="wizard" href="<mm:url referids="referrer" page="/mmbase/editwizard/jsp/wizard.jsp">
            <mm:param name="wizard"><%= getParam(editwizards_url,"wizard=","&") %></mm:param>
            <mm:param name="objectnumber">new</mm:param>
            </mm:url>"><img title="Nieuw" height="20" width="20" border="0" src="/mmbase/edit/wizard/media/new.gif"></a></td>
	<td class="right">
	<a href="<mm:url referids="referrer" page="/mmapps/editwizard/jsp/list.jsp"> 
           <mm:param name="wizard"><%= getParam(editwizards_url,"wizard=","&") %></mm:param> 
 	   	   <mm:param name="directions"><%= getParam(editwizards_url,"directions=","&") %></mm:param>
		   </mm:url>" onClick="return openListNodes(this,'<%= fullField1 %>','<%= minField1 %>','<%= fullField2 %>','<%= minField2 %>','<%= fullPath %>','<%= minPath %>', '<%= fullConstraints %>', '<%= minConstraints %>');">     
           <img title="Zoek" height="20" width="20" border="0" src="/mmbase/edit/wizard/media/search.gif"></a></td>
	</tr>
	</table>
	</form>
</td></tr></table>
</mm:field> <%-- editwizards.url --%>
</mm:list> <%-- mmbaseusers,groups,editwizards --%>
</div>
</body>
</html>
