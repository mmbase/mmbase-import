<% String title = "Edit relation"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">
<%
// Twee nieuwe strings 
// String ntype = "";			// Type of node
String node_gui = "";		// GUI variable set in builder
String node_man = "";		// Nodemanager?
%>

<mm:import externid="ntype" jspvar="ntype" vartype="String" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<mm:context id="edit_relation">
<%-- Import the relation node into this context --%>
<mm:import jspvar="nr" externid="nr" required="true" />
<mm:import jspvar="ref" externid="ref" />	<%-- the 'referer': the node on the previous page --%>
<mm:import jspvar="change" externid="change" />
<mm:import jspvar="delete" externid="delete" />
<mm:node number="<%= nr %>">
<%-- Get some information about the node: its type and GUI name --%>
<mm:nodeinfo type="type" jspvar="n_type" vartype="String" write="false"><% ntype = n_type; %></mm:nodeinfo>
<mm:nodeinfo type="guinodemanager" jspvar="n_gui" vartype="String" write="false"><% node_gui = n_gui; %></mm:nodeinfo>


<!-- main table -->
<table border="0" cellspacing="0" cellpadding="3">
<tr>
  <td valign="top" width="20%">
	<!-- table with back button -->
	<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">	
	<tr>
	  <td width="24" align="right"><a href="edit_object.jsp?nr=<%= ref %>"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
	  <td><a href="edit_object.jsp?nr=<%= ref %>">Back</a> to editing <b><mm:node number="<%= ref %>"><mm:nodeinfo type="type" /></mm:node></b> node</td>
	</tr><tr valign="top">
	  <td bgcolor="#FFFFFF">&nbsp;</td>
	  <td bgcolor="#FFFFFF"><mm:node number="<%= ref %>"><b><mm:field name="gui()" /></b></mm:node></td>
	</tr>
	</table>
	<!-- end table back button -->
  </td>
  <td valign="top" width="80%">
	<!-- start form -->
<form method="post" action="<mm:url referids="nr,ref" />">
<table border="0" cellspacing="0" cellpadding="3" width="580" class="table-form">
<tr bgcolor="#CCCCCC">
  <td bgcolor="#CCCCCC" align="center">&nbsp;</td>
  <td bgcolor="#CCCCCC" class="title-s">
    Edit relation
	<mm:field name="rnumber" jspvar="r_node" vartype="String" write="false">
		<mm:node number="<%= r_node %>"><b><mm:field name="gui()" /> </b> </mm:node>
	</mm:field>  
  </td>
</tr>

<mm:notpresent referid="delete">
	<tr valign="top">
	  <td align="right"><span class="name">Source</span><br />parent</td>
	  <td>
		<mm:field name="snumber" jspvar="s_node" vartype="String" write="false">
			<mm:node number="<%= s_node %>"><b><mm:field name="gui()" /></b><br /><mm:nodeinfo type="guinodemanager" /></mm:node>
		</mm:field>
	  </td>
	</tr><tr valign="top">
	  <td align="right"><span class="name">Destination</span><br />child</td>
	  <td>
		<mm:field name="dnumber" jspvar="d_node" vartype="String" write="false">
			<mm:node number="<%= d_node %>"><b><mm:field name="gui()" /></b><br /><mm:nodeinfo type="guinodemanager" /></mm:node>
		</mm:field>
	  </td>
	</tr><tr valign="top">
	  <td class="name" align="right">Relation kind</td>
	  <td>
		<mm:field name="rnumber" jspvar="r_node" vartype="String" write="false">
			<mm:node number="<%= r_node %>"><mm:field name="gui()" /></mm:node>
		</mm:field>
	  </td>
	</tr>
</mm:notpresent>

<mm:present referid="delete">
	<tr>
	  <td>&nbsp;</td>
	  <td>
		<mm:deletenode number="<%= nr %>" />
		<p class="message">The relation is removed.</p>
		</td>
	</tr>
</mm:present>

<mm:present referid="change">
	<tr>
	  <td>&nbsp;</td>
	  <td>
		<mm:fieldlist type="edit">
			<mm:fieldinfo type="useinput" />
		</mm:fieldlist>
		<p class="message">The relation is changed.</p>
		</td>
	</tr>
</mm:present>

<mm:notpresent referid="delete">
	<mm:maywrite>
	<mm:fieldlist type="edit">
		<tr valign="top">
		  <td align="right"><span class="name"><mm:fieldinfo type="guiname" /></span><br /><mm:fieldinfo type="name" /></td>
		  <td><mm:fieldinfo type="input" />&nbsp;</td>
		</tr>
	</mm:fieldlist>
	<tr>
	  <td align="right"><input type="submit" name="change" value="Change" /></td>
	  <td>Change relation  &nbsp;&nbsp;&nbsp;&nbsp; 
	    <mm:maydelete><input type="submit" name="delete" value="Delete" /> Remove relation</mm:maydelete>
	  </td>
	</tr>
	</mm:maywrite>
	<mm:maywrite inverse="true">
	<mm:fieldlist type="edit">
		<tr valign="top">
		  <td align="right"><span class="name"><mm:fieldinfo type="guiname" /></span><br /><mm:fieldinfo type="name" /></td>
		  <td><mm:fieldinfo type="guivalue" />&nbsp;</td>
		</tr>
	</mm:fieldlist>
	<tr>
	  <td align="right">&nbsp;</td>
	  <td><p class="message">You are not allowed to change or delete this relation.</p></td>
	</tr>
	</mm:maywrite>
</mm:notpresent>



</table>
</form>

  </td>
</tr></table>
<!-- end of main table -->
</mm:node>
</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
