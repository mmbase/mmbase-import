<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="ntype" />
<mm:import externid="action" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Delete more nodes</title>
<script type="text/javascript" language="javascript">
/* <![CDATA[ */
/* Check all checkboxes */
function markAll() {
	var flag = true;
	for(var i=0; i < document.theform.IDs.length; i++) { document.theform.IDs[i].checked = true; }
}

/* Toggle visibility */
function toggle(targetId){
  if (document.getElementById){
  		target = document.getElementById(targetId);
  			if (target.style.display == "none"){
  				target.style.display = "";
  			} else {
  				target.style.display = "none";
  			}
  	}
}

/* ]]> */
</script>
</head>
<body>
<div id="top">[<a href="<mm:url page="index.jsp" referids="ntype" />">back to my_editors</a>]</div>
<mm:present referid="ntype">

<mm:present referid="action">
<ol class="message">
<% 
String[] nodeIDs = request.getParameterValues("IDs");
if (nodeIDs != null && nodeIDs.length != 0) {
	for (int i = 0; i < nodeIDs.length; i++) {
%>
  <mm:compare referid="action" value="Delete selected">
	<mm:node number="<%= nodeIDs[i] %>" notfound="skipbody">
	  <li>[<%= nodeIDs[i] %>] <mm:function name="gui" /> is deleted.</li>
	  <mm:deletenode deleterelations="true" />
	</mm:node>
  </mm:compare>
<%
	}
}
%>
</ol>
</mm:present><%-- /action --%>


<form action="<mm:url referids="ntype" />" method="post" name="theform" id="theform">
<mm:fieldlist type="list" nodetype="$ntype"><mm:import id="span" reset="true"><mm:size /></mm:import></mm:fieldlist>
<mm:listnodescontainer type="$ntype" id="node">
<mm:import id="totalsize"><mm:size /></mm:import>
<mm:maxnumber value="100" />

<mm:listnodes>
  <mm:first>
  <table><tr>
	<td>&nbsp;</td>
	<mm:fieldlist type="list" nodetype="$ntype">
	  <td><mm:fieldinfo type="guiname" /></td>
	</mm:fieldlist>
  </tr>
  </mm:first>
  <tr>
    <td><input name="IDs" type="checkbox" value="<mm:field name="number" />" /></td>
	<mm:fieldlist type="list" nodetype="$ntype">
	  <td><mm:fieldinfo type="guivalue" /></td>
	</mm:fieldlist>
  </tr>
  <mm:last></table></mm:last>
</mm:listnodes>

</mm:listnodescontainer>

<mm:compare referid="totalsize" value="0"><p>No nodes found or left</p></mm:compare>

<input name="action" id="action" type="submit" value="Delete selected" />
<input name="check" id="check" type="button" value="Check all" onclick="markAll();" />
</form>
</mm:present><%-- /ntype --%>


<mm:notpresent referid="ntype">
<h2>No node type</h2>
<p>You need to provide a nodetype with the parameter 'ntype'.</p>
</mm:notpresent>
</body>
</html>
</mm:cloud>
