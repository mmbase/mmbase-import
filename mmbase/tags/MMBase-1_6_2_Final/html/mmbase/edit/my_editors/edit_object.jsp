<% String title = "Edit object"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<% // Twee nieuwe strings 
String ntype = "";			// Type of node
String node_gui = "";		// GUI variable set in builder
%>

<mm:context id="change_node">
<%-- Import the node number into this context --%>
<mm:import jspvar="nr" externid="nr" required="true" />
<mm:import externid="change" />
<mm:import externid="alias_name" />
<mm:node number="<%= nr %>">
<%-- Get information about the node: type & GUI name --%>
<mm:nodeinfo type="type" jspvar="n_type" vartype="String" write="false"><% ntype = n_type; %></mm:nodeinfo>
<mm:nodeinfo type="guinodemanager" jspvar="n_gui" vartype="String" write="false"><% node_gui = n_gui; %></mm:nodeinfo>
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<form enctype="multipart/form-data" method="post" action="<mm:url referids="nr" />">
<table border="0" cellspacing="0" cellpadding="3" class="table-form">
<tr bgcolor="#CCCCCC">
  <td class="title-m" align="right" valign="bottom">
    <mm:maydelete><a href="delete_object.jsp?nr=<%= nr %>&amp;ntype=<%= ntype %>" title="delete"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
    <%-- <mm:maycreate type="<%= ntype %>"> --%><a href="new_object.jsp?ntype=<%= ntype %>" title="new node"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a><%-- </mm:maycreate> --%>
    </td>
  <td class="title-m" valign="bottom"><b><mm:nodeinfo type="guinodemanager" /></b> (<mm:nodeinfo type="type" />): <b><mm:field name="gui()" /></b></td>
</tr>
<mm:present referid="change">
<tr>
  <td>&nbsp;</td>
  <td>
	<mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>
    <%-- When there is was a alias, create that 1 2 --%> 
    <mm:present referid="alias_name">
    	<mm:node id="new_alias">
            <mm:createalias name="${alias_name}" />
        </mm:node>
    </mm:present>
	<p class="message">The node <b><mm:field name="gui()" /></b> (<%= nr %>) is changed.</p>
  </td>
</tr>
</mm:present>    
<mm:fieldlist type="edit">
<tr valign="top">
  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
  <td><mm:fieldinfo type="input" />&nbsp;</td>
</tr>
</mm:fieldlist>
<tr>
  <td>&nbsp;</td>
  <td><input type="submit" name="change" value="Change" /></td>
</tr>
<%-- Aliases --%>
<tr valign="top">
  <td align="right" class="name">Aliases</td>
  <td>
	<mm:aliaslist id="alias"><mm:write /> 
	<!-- <a href="" title="edit alias"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a> -->
	</mm:aliaslist>
  </td>
<tr><tr valign="top">
  <td align="right" class="name">New alias</td>
  <td><input type="text" name="alias_name" size="32" maxlength="255" /><br />An alias is an optional identifier for a node</td>
</tr>

<%-- Relations --%>

<mm:context id ="relaties">
<% // Vergelijk een typerel (alle relaties zijn van het type typrel, toch?) veld met 1 van deze node
String mayrelate_type[] = new String[99]; 
String mayrelate_kind[] = new String[99]; 
int i = 0;
%>
<mm:listnodes type="typerel" jspvar="list_node">
    <%
    // Source en destination van deze (node van het type) typerel
    int r_number = list_node.getIntValue("rnumber");
    int s_number = list_node.getIntValue("snumber");
    int d_number = list_node.getIntValue("dnumber");

    // De source en distination node en hun naam
    Node s_node = wolk.getNode(s_number);
    String s_nodes_name = s_node.getStringValue("name");
    Node d_node = wolk.getNode(d_number);
    String d_nodes_name = d_node.getStringValue("name");
  
    Node r_node = null;
    
	// Relation node
    // En 1 van hen matched met de node_type van deze pagina -> mag je linken
    if (s_nodes_name.equals(ntype)) {
      r_node = wolk.getNode(r_number);			// Soort relatie
      String r_nodes_name = r_node.getStringValue("sname");
      
      mayrelate_type[i] = d_nodes_name;
      mayrelate_kind[i] = r_nodes_name;
      i++;
    } 
    else if (d_nodes_name.equals(ntype)) {
      r_node = wolk.getNode(r_number);
      String r_nodes_name = r_node.getStringValue("sname");

      mayrelate_type[i] = s_nodes_name;
      mayrelate_kind[i] = r_nodes_name;
      i++;
    } 
    %>
</mm:listnodes>

<% if (i > 0) { %>
<tr>
  <td bgcolor="#CCCCCC" colspan="2" class="title-m">Relations</td>
</tr><tr>
  <td colspan="2">  
  <table width="100%" border="0" cellspacing="0" cellpadding="3">
<% }
// Loop over the allowed relations types
for (int j = 0; j < i; j++) {
%>
  <tr bgcolor="#EFEFEF"> <%-- nr: current node number, ntype: kind of nodetype to relate with, kind: kind of relation --%>
	<td align="right" width="50"><a href="relate_object.jsp?nr=<%= nr %>&amp;ntype=<%= mayrelate_type[j] %>&amp;kind=<%= mayrelate_kind[j] %>" title="new relation"><img src="img/mmbase-new.gif" alt="new relation" width="21" height="20" border="0" /></a></td>
	<td><b><%= mayrelate_type[j] %></b> (<%= mayrelate_kind[j] %>)</td>
    <td>&nbsp;</td>
  </tr>
	<mm:listrelations type="<%= mayrelate_type[j] %>" role="<%= mayrelate_kind[j] %>">
	  <tr valign="bottom" <mm:odd>bgcolor="#FFFFFF"</mm:odd>>
	    <td nowrap width="50" align="right">
	      <%-- go to node and edit relation (includes delete) --%>
	      <mm:relatednode>
	        <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-left.gif" alt="edit object" width="21" height="20" border="0" /></a></mm:maywrite>
	      </mm:relatednode>
		  <a href="edit_relation.jsp?nr=<mm:field name="number" />&amp;ref=<%= nr %>" title="edit or delete relation"><img src="img/mmbase-edit.gif" alt="edit relation" width="21" height="20" border="0" /></a>
	    </td>
	    <td><mm:relatednode> <mm:field name="gui()" /> </mm:relatednode></td>
	    <td align="right"> <!-- leeg ? --> </td>
	  </tr>
	</mm:listrelations>
<% }
if (i > 0) { %>
</table>
<% } %>
</mm:context>

</td>
</tr>
</table>
</form>


</mm:node>
</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
