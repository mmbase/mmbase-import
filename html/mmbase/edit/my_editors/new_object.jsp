<% String title = "Create new node"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">

<mm:context id="createandrelate_node">

<mm:import externid="ntype"		jspvar="ntype" required="true" />	<%-- create node of this type--%>
<mm:import externid="alias_name" />									<%-- .. and with this alias --%>

<mm:import externid="nr"		jspvar="nr" />		<%-- create relation with this node nr --%>
<mm:import externid="rkind"		jspvar="rkind" />	<%-- create relation of this kind --%>
<mm:import externid="dir" 		jspvar="dir" />		<%-- create relation with this direction: parent, child --%>

<mm:import externid="save_it" />					<%-- save node (of type = ntype) --%>

<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<!-- main table -->
<table border="0" cellspacing="0" cellpadding="3">
<tr>
  <td valign="top" width="20%">
	<!-- table with back button -->
	<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">	
	<tr>
	  <td width="24" align="right"><a href="index.jsp?ntype=<%= ntype %>"><img src="img/mmbase-left.gif" alt="go back" width="21" height="20" border="0" /></a></td>
	  <td>To <b><%= ntype %></b> overview</td>
	</tr>
	<mm:present referid="nr">
		<tr>
		  <td width="24" align="right">
			<a href="relate_object.jsp?nr=<%= nr %>&amp;ntype=<%= ntype %>&amp;rkind=<%= rkind %>&amp;dir=<mm:write referid="dir" />" title="search node for new relation"><img src="img/mmbase-search.gif" alt="search node" width="21" height="20" border="0" /></a>
		  </td>
		  <td>Search node of type <b><%= ntype %></b> </td>
		</tr>
		<tr>
		  <td width="24" align="right"><a href="edit_object.jsp?nr=<%= nr %>"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
		  <td><a href="edit_object.jsp?nr=<%= nr %>">Back</a> to editing <mm:node number="<%= nr %>"><mm:nodeinfo type="type" /></mm:node> object</td>
		</tr>
		<tr valign="top">
		  <td>&nbsp;</td>
		  <td><mm:node number="$nr"><b><mm:field name="gui()" /></b></mm:node></td>
		</tr>
	</mm:present>
	</table>
	<!-- end table back button -->  
  </td>
  <td valign="top" width="80%">
  <!-- start edit form -->

<%-- Start 1: do we have a node type ? --%>
<mm:present referid="ntype">
	<mm:notpresent referid="save_it">
		<mm:present referid="nr">
			<form enctype="multipart/form-data" name="create" action="new_object.jsp?ntype=<%= ntype %>&nr=<%= nr %>&rkind=<%= rkind %>&dir=<%= dir %>" method="post">
		</mm:present>
		<mm:notpresent referid="nr">
			<form enctype="multipart/form-data" name="create" action="new_object.jsp?ntype=<%= ntype %>" method="post">
		</mm:notpresent>
		
		<table border="0" cellspacing="0" cellpadding="3" class="table-form">
		<tr bgcolor="#CCCCCC">
		  <td align="center">&nbsp;</td>
		  <td class="title-m">New node of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)</td>
		</tr>
		<mm:fieldlist nodetype="$ntype" type="edit" id="edit_newnode">
			<tr valign="top">
				<td align="right"><span class="name"><mm:fieldinfo type="guiname" /></span><br /><mm:fieldinfo type="name" /></td>
				<td><mm:fieldinfo type="input" /></td>
			</tr>
		</mm:fieldlist>
		<tr>
		  <td>&nbsp;</td>
		  <td class="title-m"> <input type="submit" name="save_it" value="Save" /> </td>
		</tr>
		<tr valign="top">
		  <td align="right" class="name">Alias</td>
		  <td><input class="small" type="text" name="alias_name" size="32" maxlength="255" /><br />An alias is an optional identifier for a node</td>
		</tr>
		</table>
		</form>
	</mm:notpresent>	<%-- end of notpresent save_it --%>


<%-- Step 2: Save the new node --%>
	<mm:present referid="save_it">
	<mm:compare referid="save_it" value="Save">
		<mm:createnode type="$ntype" id="new_node">
		  <mm:fieldlist type="edit" id="edit_newnode">
			<mm:fieldinfo type="useinput" />
		  </mm:fieldlist>
		</mm:createnode>
	
		<%-- When there is an alias, create that one 2 --%> 
		<mm:present referid="alias_name">
			<mm:node id="new_alias" referid="new_node" >
				<mm:createalias name="$alias_name" />
			</mm:node>
		</mm:present>
	
		<p class="message">Your new node is saved.</p>

		<mm:node referid="new_node">
			<%-- put the node with a new number in the context to be able to create a relation --%>
			<mm:notpresent referid="rnr">
				<mm:import id="rnr"><mm:field name="number" /></mm:import>
			</mm:notpresent>
		</mm:node>
	</mm:compare>	
	</mm:present>	<%-- end save_it --%>

</mm:present>		<%-- end of ntype --%>
<%-- ### End saving and showing node --%>

<%-- ### Step 3: Create relation with rnr --%>
<mm:notpresent referid="rnr">
	<mm:import externid="rnr" />
</mm:notpresent>

<%-- new node --%>
<mm:present referid="rnr">
<mm:node number="$rnr">
	<table border="0" cellspacing="0" cellpadding="3" class="table-form">
	<tr bgcolor="#CCCCCC">
	  <td align="right" class="title-s">New node</td>
	  <td>
		<table border="0" width="100%" cellspacing="0" cellpadding="3">
		<tr>
		  <td class="title-s"><b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)</td>
		  <td align="right">
			<a href="new_object.jsp?ntype=<%= ntype %>" title="another new node"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a>
			<mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit this node"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></mm:maywrite>
			<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete this node"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
		  </td>
		</tr>
		</table>
	  </td>
	</tr>
	<mm:fieldlist type="list">
		<tr valign="top">
		  <td align="right"><span class="name"><mm:fieldinfo type="guiname" /></span><br /><mm:fieldinfo type="name" /></td>
		  <td><mm:fieldinfo type="guivalue" />&nbsp;</td>
		</tr>
	</mm:fieldlist>
	<mm:present referid="alias_name">
		<tr valign="top">
		  <td align="right" class="name">Alias</td>
		  <td><mm:aliaslist id="aliassess"><mm:write /></mm:aliaslist></td>
		</tr>
	</mm:present>
	</table>
</mm:node>
</mm:present>

<mm:present referid="nr">		<%-- when there is nr, we assume: rkind, role --%>
	<%@ include file="inc_relate.jsp" %>
</mm:present>

</td></tr>
</table>
<!-- end main table -->

</mm:context>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
