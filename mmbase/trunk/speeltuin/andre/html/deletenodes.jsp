<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="basic user">
<mm:import externid="action" />
<mm:import externid="node_type" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Delete node(s) of a certain type</title>
<style type="text/css">
body
{
	font-family: "Lucida Grande", Geneva, Helvetica, Arial, sans-serif;
	font-size: 12px;
	background-color: #EFEFEF;
}
</style>
<script type="text/javascript" language="javascript">
// <![CDATA[
function markAll() {
	var flag = true;
	for(var i=0; i < document.my_form.IDs.length; i++) { document.my_form.IDs[i].checked = true; }
}
// ]]>
</script>
</head>
<body>
<a href="<mm:url />">back to start</a> | <strong>Delete node(s) of a certain type</strong>
<hr />
<mm:notpresent referid="node_type">
	<form action="<mm:url />" method="post">
	<select name="node_type" id="node_type">
	<mm:listnodes type="typedef">
		<option label="<mm:field name="name" />" value="<mm:field name="name" />"><mm:field name="name" /></option>
	</mm:listnodes>
	</select>
	<input type="submit" name="action" value="Select" />
	</form>
</mm:notpresent>

<mm:present referid="action">
	<ol>
	<% 
	String[] nodeIDs = request.getParameterValues("IDs");
	if (nodeIDs != null && nodeIDs.length != 0) {
		for (int i = 0; i < nodeIDs.length; i++) {
	%>
		<mm:compare referid="action" value="Delete!">
			<mm:node number="<%= nodeIDs[i] %>" notfound="skipbody">
				<li><strong><mm:field name="gui()" /></strong> [<%= nodeIDs[i] %>] is verwijderd</li>
				<mm:deletenode deleterelations="true" />
			</mm:node>
		</mm:compare>
	<%
		}
	}
	%>
	</ol>
</mm:present>

<mm:present referid="node_type">
	<form name="my_form" id="my_form" action="<mm:url referids="node_type" />" method="post">
	<input type="button" name="check" value="Check all" onclick="markAll();" />
	<mm:listnodes type="$node_type">
		<mm:first><ol></mm:first>
		<li>
		  <input type="checkbox" name="IDs" value="<mm:field name="number" />" /> <strong><mm:field name="gui()" /></strong>
		  [<mm:field name="number" />] (# of relations : <mm:countrelations />)
		</li>
		<mm:last></ol></mm:last>
	</mm:listnodes>
	<input type="submit" name="action" value="Delete!" />
	</form>
</mm:present>

</body>
</html>
</mm:cloud>