<% String title = "Edit relation"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<%
// Twee nieuwe strings 
String ntype = "";			// Type of node
String node_gui = "";		// GUI variable set in builder
String node_man = "";		// Nodemanager?
%>

<mm:context id="edit_relation">
<%-- Import the relation node into this context --%>
<mm:import jspvar="nr" externid="nr" required="true" />
<mm:import jspvar="ref" externid="ref" />	<%-- the 'referer': the node on the previous page --%>
<mm:import jspvar="action" externid="action" />
<mm:node number="<%= nr %>">
<%-- Get some information about the node: its type and GUI name --%>
<mm:nodeinfo type="type" jspvar="n_type" vartype="String" write="false"><% ntype = n_type; %></mm:nodeinfo>
<mm:nodeinfo type="guinodemanager" jspvar="n_gui" vartype="String" write="false"><% node_gui = n_gui; %></mm:nodeinfo>

<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>


<form method="post" action="<mm:url referids="nr,ref" />">
<table border="0" cellspacing="0" cellpadding="4" class="table-form">
<tr bgcolor="#CCCCCC">
  <td>&nbsp;</td>
  <td class="title-m">Related objects</td>
</tr>
<tr valign="top">
  <td class="name" align="right">Source</td>
  <td>
	<mm:field name="snumber" jspvar="s_node" vartype="String" write="false">
		<mm:node number="<%= s_node %>"><b><mm:field name="gui()" /></b><br /><mm:nodeinfo type="guinodemanager" /></mm:node>
	</mm:field>
  </td>
</tr><tr valign="top">
  <td class="name" align="right">Destination</td>
  <td>
	<mm:field name="dnumber" jspvar="d_node" vartype="String" write="false">
		<mm:node number="<%= d_node %>"><b><mm:field name="gui()" /></b><br /><mm:nodeinfo type="guinodemanager" /></mm:node>
	</mm:field>
  </td>
</tr><tr valign="top">
  <td class="name" align="right">Relation kind</td>
  <td>
	<mm:field name="rnumber" jspvar="r_node" vartype="String" write="false">
		<mm:node number="<%= r_node %>"><mm:field name="gui()" /> <!-- <br /><mm:nodeinfo type="type" /> --></mm:node>
	</mm:field>
  </td>
</tr>

<mm:notpresent referid="action">
	<mm:fieldlist type="edit">
		<tr valign="top">
		  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
		  <td><mm:fieldinfo type="input" />&nbsp;</td>
		</tr>
	</mm:fieldlist>
	<tr>
	  <td align="right"><input type="submit" name="action" value="Change" /></td><td> Change relation  </td>
	</tr><tr>
	  <td align="right"><input type="submit" name="action" value="Delete" /></td><td>Remove relation</td>
	</tr>
</mm:notpresent>

<mm:present referid="action">
	<tr>
	  <td colspan="2">
	<%
	if (action.equals("Change")) {
	%>
		<mm:fieldlist type="edit">
			<mm:fieldinfo type="useinput" />
		</mm:fieldlist>
		<p class="message">De relation is changed.</p>
	<% 
	} else if (action.equals("Delete")) {
	%>
		<mm:deletenode number="<%= nr %>" />
		<p class="message">The relation is removed.</p>
	<% } %>
		</td>
	</tr>
</mm:present>

<tr>
  <td align="right"><a href="edit_object.jsp?nr=<%= ref %>"><img src="img/mmbase-left.gif" alt="go back" width="21" height="20" border="0"></a></td>
  <td>
	Back to the <a href="edit_object.jsp?nr=<%= ref %>">edit page</a> of 
	<mm:node number="<%= ref %>"><b><mm:field name="gui()" /></b></mm:node>.
  </td>
</tr>
</table>
</form>


</mm:node>
</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
