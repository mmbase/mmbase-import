<% String title = "New node"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">

<mm:context id="create_node">

<mm:import jspvar="bewaar" externid ="bewaar" />
<mm:import externid="alias_name" />
<mm:import jspvar="ntype" externid="ntype" />

<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>


<%-- Start: do we have a node type ? --%>
<mm:present referid="ntype">
  <mm:notpresent referid="bewaar">
    <form enctype="multipart/form-data" name="create" action="new_object.jsp?ntype=<%= ntype %>" method="post">
	<table border="0" cellspacing="0" cellpadding="3" class="table-form">
	<tr bgcolor="#CCCCCC">
	  <td align="right">&nbsp;</td>
	  <td class="title-m">New node of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)</td>
	</tr>
	<mm:fieldlist nodetype="<%= ntype %>" type="edit">
		<tr valign="top">
			<td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
			<td><mm:fieldinfo type="input" /></td>
		</tr>
	</mm:fieldlist>
	<tr>
	  <td>&nbsp;</td>
	  <td><input type="submit" name="bewaar" value="Bewaar" /></td>
	</tr>
	<tr valign="top">
	  <td align="right" class="name">Alias</td>
	  <td><input type="text" name="alias_name" size="32" maxlength="255" /><br />An alias is an optional identifier for a node</td>
	</tr>
    </table>
    </form>
  </mm:notpresent>
</mm:present>

<%-- Save the new node and show it --%>
<mm:present referid="ntype">
  <mm:present referid="bewaar">
    <mm:createnode type="<%= ntype %>" id="new_node">
      <mm:fieldlist type="edit">
		<mm:fieldinfo type="useinput" />
      </mm:fieldlist>
    </mm:createnode>

    <%-- When there is an alias, create that 1 2 --%> 
    <mm:present referid="alias_name">
    	<mm:node id="new_alias" referid="new_node" >
            <mm:createalias name="${alias_name}" />
        </mm:node>
    </mm:present>

	
	<mm:node referid="new_node">
	<br />
	<table border="0" cellspacing="0" cellpadding="3" class="table-form">
	<tr bgcolor="#CCCCCC">
	  <td align="right">
	    <a href="new_object.jsp?ntype=<%= ntype %>" title="another new node"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a>
	    <a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit this node"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a>
		<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete this node"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
	  </td>
	  <td class="title-m"><b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)</td>
	</tr>
   	<tr valign="top">
   		  <td align="right" class="name">&nbsp;</td>
   		  <td><p class="message">Your new node of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b> (<%= ntype %>)
   		     is saved with the following values.</p></td>
	</tr>
	<mm:fieldlist type="list">
    	<tr valign="top">
    	  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
    	  <td><mm:fieldinfo type="guivalue" />&nbsp;</td>
    	</tr>
    </mm:fieldlist>
    <mm:present referid="alias_name">
    	<tr valign="top">
    	  <td align="right" class="name">Alias</td>
    	  <td><mm:aliaslist id="aliasses"><mm:write /></mm:aliaslist></td>
    	</tr>
    </mm:present>
	<tr>
	  <td>&nbsp;</td>
	  <td nowrap="nowrap">
		
	  </td>
	</tr>
	</table>    
    </mm:node>
    

  </mm:present>
</mm:present>
<%-- End saving and showing node --%>

</mm:context>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
