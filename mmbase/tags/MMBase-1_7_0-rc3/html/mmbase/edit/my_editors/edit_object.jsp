<% String title = "Edit node"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="loginpage" loginpage="login.jsp" rank="basic user">
<% // Two new strings 
String ntype = "";			// Type of node
String node_gui = "";		// GUI variable set in builder
String path1 = "";
%>

<mm:context id="change_node">
<mm:import jspvar="nr" externid="nr" id="nr" />		<%-- the node we're going to edit --%>

<mm:import externid="change" />
<mm:import externid="alias_name" />

<mm:node number="$nr" notfound="skipbody">
<mm:import id="nodefound" />
<%-- Get information about the node: type & GUI name --%>
<mm:nodeinfo type="type" jspvar="n_type" vartype="String" write="false"><% ntype = n_type; %></mm:nodeinfo>
<mm:nodeinfo type="guinodemanager" jspvar="n_gui" vartype="String" write="false"><% node_gui = n_gui; %></mm:nodeinfo>
<% 
path1 = ntype;		// 1st piece of Hans & Gretchel path
title = "Edit " + ntype + " node";
%>
<%@ include file="inc_head.jsp" %>

<!-- main table -->
<table border="0" cellspacing="0" cellpadding="3">
<tr>
  <td valign="top" width="20%">

<%@ include file="inc_relations.jsp" %>
	
<!-- table about icons -->
<br />
<table border="0" cellpadding="0" cellspacing="0" align="center">
  <tr align="left">
    <td colspan="2">&nbsp;</td>
    <td rowspan="3" nowrap="nowrap">&nbsp;<b class="title-ss">About the icons</b></td>
    <td colspan="2">&nbsp;</td>
  </tr><tr align="left">
    <td bgcolor="#000000" colspan="2"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td bgcolor="#000000" colspan="2"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr align="left">
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td width="20">&nbsp;</td>
    <td width="20">&nbsp;</td>
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr align="left">
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
    <td colspan="3"><!- table in table --><table width="100%" border="0" cellspacing="0" cellpadding="4">
		<tr>
		  <td align="right" width="24"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a></td>
		  <td colspan="2" nowrap="nowrap"> Search node to relate to </td>
		</tr>
		<tr>
		  <td align="right" width="24"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a></td>
		  <td colspan="2" nowrap="nowrap"> Create new node and relate </td>
		</tr>
		<tr>
		  <td align="right" width="24"><img src="img/mmbase-relation-left.gif" alt="relation" width="21" height="20" border="0" /></a></td>
		  <td colspan="2" nowrap="nowrap"> Edit relation </td>
		</tr>
		<tr>
		  <td align="right" width="24"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></td>
		  <td colspan="2" nowrap="nowrap"> Edit node </td>
		</tr>
		<tr>
		  <td align="right" width="24"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></td>
		  <td colspan="2" nowrap="nowrap"> Delete node </td>
		</tr>
	  </table><!- end table in table --></td>
    <td bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr><tr>
    <td colspan="5" bgcolor="#000000"><img src="img/spacer.gif" alt="" width="1" height="1" /></td>
  </tr>
</table>
<!-- end about the icons -->
  </td>
  <td valign="top" width="80%">
  <!-- start edit form -->

<form enctype="multipart/form-data" method="post" action="<mm:url referids="nr" />">
<table border="0" cellspacing="0" cellpadding="3" class="table-form">
<tr bgcolor="#CCCCCC">
  <td align="center">&nbsp;</td>
  <td valign="top">
    <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr>
      <td class="title-s">Edit node of type <b><mm:nodeinfo type="guinodemanager" /></b> (<mm:nodeinfo type="type" />)</td>
      <td align="right">
		<mm:maydelete><a href="delete_object.jsp?nr=<%= nr %>&amp;ntype=<%= ntype %>" title="delete"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
	  </td>
    </tr>
    </table>
  </td>
</tr>
<mm:present referid="change">
<tr>
  <td>&nbsp;</td>
  <td>
	<mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>
    <%-- When there is was a alias, create that 1 2 --%> 
    <mm:present referid="alias_name">
    	<mm:node id="new_alias">
        <mm:createalias name="$alias_name" />
      </mm:node>
    </mm:present>
	<p class="message">The node <b><mm:field name="gui()" /></b> (<%= nr %>) is changed.</p>
  </td>
</tr>
</mm:present>
<mm:maywrite>
<mm:fieldlist type="edit">
<tr valign="top">
  <td align="right"><span class="name"><mm:fieldinfo type="guiname" /></span><br /><mm:fieldinfo type="name" /></td>
  <td><mm:fieldinfo type="input" />&nbsp;</td>
</tr>
</mm:fieldlist>
<tr>
  <td>&nbsp;</td>
  <td><input type="submit" name="change" value="Change" /><p>&nbsp;</p></td>
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
  <td>&nbsp;</td>
  <td><p class="message">You are not allowed to edit his node.</p><p>&nbsp;</p></td>
</tr>
</mm:maywrite>
<%-- Aliases --%>
<tr bgcolor="#CCCCCC" valign="top">
  <td align="right" class="title-s">Aliases</td>
  <td class="title-s">
  	<% String my_constr = ""; %>
	<mm:aliaslist id="alias">
		<b><mm:write referid="alias" jspvar="the_alias"><% my_constr = "name='" + (String)the_alias + "'"; %></mm:write></b>
		<mm:listnodes type="oalias" constraints="<%= my_constr %>">
			<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete alias"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete> 
		</mm:listnodes> 
		<mm:last inverse="true"><br /></mm:last>
	</mm:aliaslist>
  </td>
</tr><tr valign="top">
  <td align="right" class="name">New alias</td>
  <td><input type="text" name="alias_name" size="80" maxlength="255" class="small" /><br />An alias is an optional identifier for a node</td>
</tr>
</table>
</form>

</td></tr>
</table>
<!-- end main table -->

</mm:node>
<mm:notpresent referid="nodefound">
<% 
path1 = null;	// 1st piece of Hans & Gretchel path
title = "Node not found";
%>
<%@ include file="inc_head.jsp" %>
<p>&nbsp;</p>
<p class="message">The node you were looking for <mm:present referid="nr">with number <mm:write referid="nr" /></mm:present> could not be found.</p>
<p>&nbsp;</p>
</mm:notpresent>
</mm:context>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
