<% String title = "Delete object"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<mm:import externid="nr" jspvar="nr" required="true" />
<mm:import externid="delete" />
<%
String node_type = "";
String node_gui = "";
%>
<mm:present referid="nr">
<mm:node number="$nr" id="the_node">

	<mm:nodeinfo type="type" jspvar="n_type" vartype="String" write="false"><% node_type = n_type; %></mm:nodeinfo>
	<mm:nodeinfo type="guinodemanager" jspvar="n_gui" vartype="String" write="false"><% node_gui = n_gui; %></mm:nodeinfo>
	<% String path1 = node_type;		// Eerst stukje van kruimelpad %>
	<%@ include file="inc_head.jsp" %> 

	<%-- Check: are you sure --%>
	<mm:notpresent referid="delete">

		<form method="post" action="<mm:url referids="nr" />">
		<table border="0" cellspacing="0" cellpadding="4" class="table-form">
		<tr>
		  <td colspan="2" bgcolor="#CCCCCC" class="title-m">
		  	Delete node <b><mm:field name="gui()" /></b> of type <%= node_gui %> (<%= node_type %>)
		  </td>
		</tr>
		<mm:fieldlist type="list">
			<tr valign="top">
			  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
			  <td><mm:fieldinfo type="guivalue" /> &nbsp;</td>
			</tr>
		</mm:fieldlist>
    	<tr valign="top">
    	  <td align="right" class="name">Alias</td>
    	  <td><mm:aliaslist node="the_node" id="aliasses"><mm:write /></mm:aliaslist></td>
    	</tr>
		<tr>
		  <td>&nbsp;</td>
		  <td>
			<mm:import id="nr_relations" jspvar="nr_rels" vartype="String"><mm:countrelations /></mm:import>
			<% 
			int nr_rel = Integer.parseInt(nr_rels);
			if (nr_rel > 0) { 
			%>
				<p class="message">This node has <%= nr_rel %> relation(s) with other node(s).<br />
				<input type="submit" name="delete" value="Delete_with_relations" /></p>
			<% } else { %>
				<input type="submit" name="delete" value="Delete" />
			<% } %>
		  </td>
		</tr>
		</table>
		</form>

	</mm:notpresent>	<%-- end notpresent delete --%>
</mm:node>

	<%-- Delete the node --%>
	<mm:present referid="delete">
		<mm:deletenode number="<%= nr %>" deleterelations="true" />
		<p>&nbsp;</p>
		<p class="message">The node <%= node_type %> <b><%= nr %></b> is deleted.<br />
		Back to the <a href="index.jsp?type=<%= node_type %>">overview of <%= node_type %></a>.</p>
		<p>&nbsp;</p>
	</mm:present>

</mm:present>	<%-- end present nr --%>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
