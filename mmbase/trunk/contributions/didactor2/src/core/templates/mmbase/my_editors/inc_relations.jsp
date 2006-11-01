<%-- Relations --%>
<mm:context id="relaties">
<table width="230" border="0" cellspacing="0" cellpadding="3" class="table-left">
<% // Stuff about relations: possible relations and the ones that are allready made
Node node = wolk.getNode(nr);
NodeManager nodeNM = node.getNodeManager();

// Get the allowed relations of this type of node (when this node is the destination node)
RelationManagerIterator relIterator = nodeNM.getAllowedRelations( (NodeManager) null, null, "source").relationManagerIterator();
int c= 0;		// Lay-out counter
while(relIterator.hasNext()) {
	RelationManager relationManager = relIterator.nextRelationManager();
	
	NodeManager otherManager;
	try {
		otherManager = relationManager.getSourceManager();
	} catch (NotFoundException e) {
		continue;
	}

	c++;

	String role = relationManager.getForwardRole();
	String otherManagerName = otherManager.getName();
	
	if (c==1) {		// Display start of this table when parent relations are possible
%>
	<tr bgcolor="#CCCCCC">
	  <td><img src="img/spacer.gif" alt="" width="21" height="20" border="0" /></td>
	  <td colspan="2" class="title-s">Relations (parents)</td>
	</tr>
	<% } // end of c==1 
	if (c!=1) { %>
	<tr><td colspan="3" bgcolor="#CCCCCC"><img src="img/spacer.gif" alt="" width="224" height="1" border="0" /></td></tr>
	<% } // end of c!=1 %>
	<%-- print kind of node to relate to and kind of relation --%>
	<tr bgcolor="#EFEFEF">	<%-- print kind of node to relate to and kind of relation --%>
	  <td nowrap="nowrap">&nbsp;</td>
	  <td nowrap="nowrap"><b><%= otherManager.getGUIName()%></b> <%= otherManagerName %><br />
  	    <%= role %> <!-- <%= relationManager.getForwardGUIName() %>/<%= relationManager.getReciprocalGUIName() %> -->
  	  </td>
	  <td nowrap="nowrap" align="right">
		<a href="relate_object.jsp?nr=<%= nr %>&amp;ntype=<%= otherManagerName %>&amp;rkind=<%= role %>&amp;dir=nwparent" title="search node for new relation"><img src="img/mmbase-search.gif" alt="search node" width="21" height="20" border="0" /></a>
		<% if (otherManager.mayCreateNode()) { %><a href="new_object.jsp?nr=<%= nr %>&amp;ntype=<%= otherManagerName %>&amp;rkind=<%= role %>&amp;dir=nwparent" title="create new node and relate"><img src="img/mmbase-new.gif" alt="new node" width="21" height="20" border="0" /></a><% } %>
	  </td>
	</tr>
<%-- MMBase taglib 1.1.0 only (MMBase v1.7)
	<mm:listrelations role="<%= role %>" type="<%= otherManagerName %>" searchdir="source">
		<mm:field name="number" id="rel_nr" write="false" />
		<mm:relatednode>
		<mm:nodeinfo type="type" id="my_type" write="false" />
		<mm:compare referid="my_type" value="<%= otherManagerName %>">
		<tr valign="bottom" bgcolor="#FFFFFF">
		  <td align="right" width="24"><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit node" width="21" height="20" border="0" /></a></mm:maywrite></td>
		  <td nowrap="nowrap"> <mm:field name="gui()" /> </td>
		  <td nowrap="nowrap" align="right"><a href="<mm:url page="edit_relation.jsp">
		  	<mm:param name="nr"><mm:write referid="rel_nr" /></mm:param>
		  	<mm:param name="ref"><%= nr %></mm:param>
		  	<mm:param name="ntype"><%= nodeNM.getName() %></mm:param>
		  	</mm:url>" title="edit or delete relation"><img src="img/mmbase-relation-right.gif" alt="edit relation" width="21" height="20" border="0" /></a>
		  </td>
		</tr>
		</mm:compare>
		<mm:remove referid="my_type" /><mm:remove referid="rel_nr" />
		</mm:relatednode>
	</mm:listrelations>
--%>
<%	// List the related nodes
	RelationIterator relationsIterator = node.getRelations(role, otherManager).relationIterator();
	while( relationsIterator.hasNext() ) {
		Relation relation = relationsIterator.nextRelation();
		Node parentNode = relation.getDestination();
		Node childNode = relation.getSource();

		String relationNr = Integer.toString(relation.getNumber());
		String childNodeNr = Integer.toString(childNode.getNumber());
		
		// skip when there happens to be a relation between 2 nodes of the same type
	   if (childNode.equals(node) && !relation.getSource().equals(relation.getDestination()) ||
	   		!otherManagerName.equals( childNode.getNodeManager().getName() ) ) continue; 
%>
	<mm:node number="<%= childNodeNr %>">
	  <tr valign="bottom" bgcolor="#FFFFFF">	
		<td align="right" width="24"><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit node" width="21" height="20" border="0" /></a></mm:maywrite></td>
		<td nowrap="nowrap"> <mm:field name="gui()" /> </td>
		<td nowrap="nowrap" align="right"><a href="<mm:url page="edit_relation.jsp">
		  	<mm:param name="nr"><%= relationNr %></mm:param>
		  	<mm:param name="ref"><%= nr %></mm:param>
		  	<mm:param name="ntype"><%= nodeNM.getName() %></mm:param>
		  	</mm:url>" title="edit or delete relation"><img src="img/mmbase-relation-right.gif" alt="edit relation" width="21" height="20" border="0" /></a></td>
	  </tr>
	</mm:node>
<%
   }
%>	

<%   
} // End of while (parent nodetypes)

// ######## Child relations of this node ########
relIterator = nodeNM.getAllowedRelations( (NodeManager) null, null, "destination").relationManagerIterator();	
c = 0;	// Lay-out counter has to be 0 again
while(relIterator.hasNext()) {
	c++;
	RelationManager relationManager = relIterator.nextRelationManager();
	
	NodeManager otherManager;
	try {
	 otherManager = relationManager.getDestinationManager();
	} catch (NotFoundException e) {
		continue;
	}
	String role = relationManager.getForwardRole();
	String otherManagerName = otherManager.getName();
	if (c==1) {
%>
	<tr bgcolor="#CCCCCC">
	  <td><img src="img/spacer.gif" alt="" width="21" height="20" border="0" /></td>
	  <td colspan="2" class="title-s">Relations (children)</td>
	</tr>
	<% } // End of c==1 	
	if (c!=1) { %>
	<tr><td colspan="3" bgcolor="#CCCCCC"><img src="img/spacer.gif" alt="" width="224" height="1" border="0" /></td></tr>
	<% } // end of c!=1 %>
	<tr bgcolor="#EFEFEF">	<%-- print kind of node to relate to and kind of relation --%>
	  <td nowrap="nowrap">&nbsp;</td>
	  <td nowrap="nowrap">
  	    <b><%= otherManager.getGUIName()%></b> <%= otherManagerName %><br /> 
  	    <%= role %> <!-- <%= relationManager.getReciprocalGUIName() %>/<%= relationManager.getForwardGUIName() %> -->
	  </td>
	  <td nowrap="nowrap" align="right">
		<a href="relate_object.jsp?nr=<%= nr %>&amp;ntype=<%= otherManagerName %>&amp;rkind=<%= role %>&amp;dir=nwchild" title="search node for new relation"><img src="img/mmbase-search.gif" alt="search node" width="21" height="20" border="0" /></a>
		<% if (otherManager.mayCreateNode()) { %><a href="new_object.jsp?nr=<%= nr %>&amp;ntype=<%= otherManagerName %>&amp;rkind=<%= role %>&amp;dir=nwchild" title="create new node and relate"><img src="img/mmbase-new.gif" alt="new node" width="21" height="20" border="0" /></a><% } %>
	  </td>
	</tr>
<%-- MMBase taglib 1.1.0 only (MMBase v1.7)
	<mm:listrelations role="<%= role %>" type="<%= otherManagerName %>" searchdir="destination">
		<mm:field name="number" id="rel_nr" write="false" />
		<mm:relatednode><mm:nodeinfo type="type" id="my_type" write="false" />
		<mm:compare referid="my_type" value="<%= otherManagerName %>">
		<tr valign="bottom" bgcolor="#FFFFFF">
		  <td align="right" width="24"><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit node" width="21" height="20" border="0" /></a></mm:maywrite></td>
		  <td nowrap="nowrap"> <mm:field name="gui()" /> </td>
		  <td nowrap="nowrap" align="right"><a href="<mm:url page="edit_relation.jsp">
		  	<mm:param name="nr"><mm:write referid="rel_nr" /></mm:param>
		  	<mm:param name="ref"><%= nr %></mm:param>
		  	<mm:param name="ntype"><%= nodeNM.getName() %></mm:param>
		  	</mm:url>" title="edit or delete relation"><img src="img/mmbase-relation-right.gif" alt="edit relation" width="21" height="20" border="0" /></a>
		  </td>
		</tr>
		</mm:compare>
		<mm:remove referid="my_type" /><mm:remove referid="rel_nr" />
		</mm:relatednode>
	</mm:listrelations>
--%>
<%	// List the related nodes
	RelationIterator relationsIterator = node.getRelations(role, otherManager).relationIterator();
	while( relationsIterator.hasNext() ) {
		Relation relation = relationsIterator.nextRelation();
		Node parentNode = relation.getDestination();
		Node childNode = relation.getSource();

		String relationNr = Integer.toString(relation.getNumber());
		String parentNodeNr = Integer.toString(parentNode.getNumber());
		
		// skip when there happens to be a relation between 2 nodes of the same type
	   if (parentNode.equals(node) && !relation.getSource().equals(relation.getDestination())
	   	|| !otherManagerName.equals( parentNode.getNodeManager().getName() ) ) continue; 
%>
	<mm:node number="<%= parentNodeNr %>">
	  <tr valign="bottom" bgcolor="#FFFFFF">	
		<td align="right" width="24"><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit node" width="21" height="20" border="0" /></a></mm:maywrite></td>
		<td nowrap="nowrap"> <mm:field name="gui()" /> </td>
		<td nowrap="nowrap" align="right"><a href="<mm:url page="edit_relation.jsp">
		  	<mm:param name="nr"><%= relationNr %></mm:param>
		  	<mm:param name="ref"><%= nr %></mm:param>
		  	<mm:param name="ntype"><%= nodeNM.getName() %></mm:param>
		  	</mm:url>" title="edit or delete relation"><img src="img/mmbase-relation-left.gif" alt="edit relation" width="21" height="20" border="0" /></a></td>
	  </tr>
	</mm:node>
<%
   }
%><% } // End while (child nodetypes) %>
</table>
</mm:context>
<%-- End of relations --%>
