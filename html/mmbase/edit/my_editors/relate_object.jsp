<% String title = "Relate objects"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">

<mm:context id="relate_node">
<mm:import externid="nr" jspvar="node_nr" required="true" />	<%-- node (nr) die een relatie wil --%>
<mm:import externid="ntype" jspvar="node_type" />				<%-- nodetype waarmee gerelateerd wordt --%>
<mm:import externid="kind" jspvar="rel_kind" />					<%-- soort relatie die gelegd wordt --%>
<mm:import externid="rnr" jspvar="rel_nr" />					<%-- de node (nr) waarmee relatie gelegd wordt --%>
<mm:import externid="search" />
<mm:import externid="s" />
<mm:import externid="verander_ok" />
<mm:import externid="new_nr" jspvar="new_nr" />

<% String path1 = node_type;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<p>Terug naar de <a href="edit_object.jsp?nr=<%= node_nr %>">edit pagina</a>.</p>


<%-- ### Start search (include?) ### --%>
<%-- Action: we do allways need a node type !!!!!!!!!!! --%>
<mm:present referid="ntype">
<mm:present referid="search">
	<%-- Create a search string to use as constraint on a list --%>
	<mm:import id="search_string"><mm:context><mm:fieldlist nodetype="$ntype" type="search"><mm:fieldinfo type="usesearchinput"><mm:isnotempty><mm:present referid="notfirst"> AND </mm:present><mm:notpresent referid="notfirst"><mm:import id="notfirst">yes</mm:import></mm:notpresent><mm:write /></mm:isnotempty></mm:fieldinfo></mm:fieldlist></mm:context></mm:import>
</mm:present>

<mm:notpresent referid="search">
	<mm:import id="search_string"><mm:write referid="s" /></mm:import>
</mm:notpresent>

<mm:import jspvar="search_str"><mm:write referid="search_string" /></mm:import>

<%-- ### Total found ### --%>
<% // Constraints in case of an empty searchstring (will get all the nodes of this type)
// We will try to use a day offset
// Get day of today in days since 01/01/1970
Date today = new Date();
int days_ofs = Integer.parseInt(dayofs);
int today_days = (int)(today.getTime() / (60 * 60 * 24)) / 1000;
int search_day = today_days - days_ofs;
int day_found = 0;

// Query the daymarkers table
String days_constraint = "daycount >=" + search_day;
String dm_node = "";			// Daymarker node number
%>
<mm:listnodes type="daymarks" constraints="<%= days_constraint %>" max="1">
	<mm:field name="mark" jspvar="m_node" vartype="String" write="false">
	<mm:field name="daycount" jspvar="day_cnt" vartype="String" write="false">
		<%
		dm_node = m_node;
		day_found = Integer.parseInt(day_cnt);
		%>
	</mm:field>
	</mm:field>
</mm:listnodes>
<% 
if (search_day < day_found) {
	// The day we were looking for is smaller than the one from the daymarkers list, maybe the first daymarker
	// Let's see if tis the first, if so make dm_node 0 (so you can find all nodes in MMBase)
%>
	<mm:list path="daymarks" fields="daymarks.mark">
		<mm:first><mm:field name="daymarks.mark" jspvar="first_dm" vartype="String" write="false">
			<% if (first_dm.equals(dm_node)) { dm_node = "0"; } %>
		</mm:field></mm:first>
	</mm:list>
<%
}
// Check and construct search constraints
if (dm_node.equals("")) {
	out.println("<p class=\"message\"><b>Error:</b> No daymarker_node!</p>");
} else {
	if (!search_str.equals("")) {
		search_str = search_str + " AND (number>" + dm_node + ")"; 
	} else {
		search_str = "number>" + dm_node; 
	}
}
%>


<%-- ### Show results ### --%>
<% 
int total_found = 0;
int colspan = 2;		// Colspan for navigation buttons below 
int list_index = 0;		// Index value of the list
%>
<mm:listnodes type="$ntype" constraints="<%= search_str %>">
	<mm:first><mm:size jspvar="list_size" vartype="Integer" write="false">
		<% total_found = list_size.intValue(); %>
	</mm:size></mm:first>
</mm:listnodes>
<mm:fieldlist type="list" nodetype="$ntype"><% colspan++; %></mm:fieldlist>
<table width="100%" border="0" cellspacing="0" cellpadding="4">
<tr bgcolor="#CCCCCC">
  <td colspan="<%= colspan - 1 %>" class="title-m"><%= total_found %> found of type 
  <mm:nodeinfo nodetype="$ntype" type="guitype" />  (<%= node_type %>)</td>
  <td>
	<a href="#search" title="search"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a>
	<a href="new_object.jsp?type=<%= node_type %>" title="new"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a>
 </td>
</tr>
<mm:listnodes id="node_number" type="$ntype" 
	directions="DOWN" orderby="number"
	max="<%= max_str %>" offset="<%= ofs_str %>"
	constraints="<%= search_str %>">
	<mm:first>
	<tr>
	  <td>&nbsp;</td>
	  <mm:fieldlist type="list" nodetype="$ntype"><td class="name"> <mm:fieldinfo type="guiname" /> </td></mm:fieldlist>
	  <td>&nbsp;</td>
	</tr>
	</mm:first>
<%-- The results are here --%>
	<tr valign="top"<mm:odd> bgcolor="#EFEFEF"</mm:odd>> 
  	  <td align="right">
  	    <mm:index jspvar="index_str" vartype="String" write="false"><% list_index = ofs + Integer.parseInt(index_str); %></mm:index>
  	    <%= list_index %>
  	  </td>
  	  <% int i = 0; %>
	  <mm:fieldlist type="list"><td><% if (i==0) { %><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"><% } %><mm:fieldinfo type="guivalue" /><% if (i==0) { %></a><% } %> </td> <% i++; %></mm:fieldlist>
  	  <td nowrap="nowrap">
  	    <a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a>
		<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
  	    <a title="relate" href="relate_object.jsp?nr=<%= node_nr %>&amp;kind=<%= rel_kind %>&amp;rnr=<mm:field name="number" />"><img src="img/mmbase-right.gif" alt="-&gt;" width="21" height="20" border="0" /></a> 
  	  </td>
	</tr>
<%-- End of results --%>
	<mm:last>
	<tr bgcolor="#FFFFFF">
	  <td colspan="<%= colspan %>" align="center">
	<% // Calculation
	int next = ofs + max;
	int prev = ofs - max;
	if (prev >= -1) { if (prev < 0 ) { prev = 0; }
	%>
		<a href="<mm:url referids="ntype,nr,kind"><mm:param name="o"><%= prev %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	<% 
	}
	if (ofs >= max) {  
	%>
		<a href="<mm:url referids="ntype,nr,kind"><mm:param value="0" name="o" /><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" border="0" /></a>
	<%
	}
	if (next < total_found) { 
	%>
		<a href="<mm:url referids="ntype,nr,kind"><mm:param name="o"><%= next %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	<% } %>
	  </td>
	</tr>
	</mm:last>
</mm:listnodes>
</table>

	<%-- ### Search form ### --%>
	<a name="search"></a>
	<form method="post" action="<mm:url referids="ntype,nr,kind" />">
	<table border="0" cellspacing="0" cellpadding="4" class="table-form">
		<tr bgcolor="#CCCCCC">
		  <td colspan="2" class="title-m">Search node of type <mm:nodeinfo nodetype="$ntype" type="guitype" /> (<%= node_type %>)</td>
		</tr>
		<tr valign="top">
		  <td align="right" class="name">Days old</td>
		  <td><input type="text" name="dayofs" value="<%= dayofs %>" size="9" maxlength="9"></td>
		</tr>
		<mm:fieldlist nodetype="$ntype" type="search">
			<tr valign="top">
			  <td align="right" class="name"><mm:fieldinfo type="guiname" /></td>
			  <td><mm:fieldinfo type="searchinput" /></td>
			</tr>
		</mm:fieldlist>
	  <tr><td align="right" colspan="2"><input type="submit" name="search" value="Search" /></td></tr>
	</table>
	</form>

</mm:present>
<%-- ### End Search include ### --%>

















<!-- ### Create relation met node met nummer: rnr - we hebben node_nr, ntype, rel_kind, rel_nr ### -->
<mm:present referid="rnr">
	<table border="0" cellspacing="0" cellpadding="4" class="table-form">
	<tr bgcolor="#CCCCCC">
	  <td>&nbsp;</td><td class="title-m">Objects related</td>
	</tr>
	<tr valign="top">
	  <td class="name" align="right">Source</td>
	  <td>
		<mm:node number="<%= node_nr %>" id="source_node">
			<b><mm:field name="gui()" /></b><br><mm:nodeinfo type="guinodemanager" />
		</mm:node> 
	  </td>
	</tr><tr valign="top">
	  <td class="name" align="right">Destination</td>
	  <td>
		<mm:node number="<%= rel_nr %>" id="dest_node">
			<b><mm:field name="gui()" /></b><br><mm:nodeinfo type="guinodemanager" />
		</mm:node>
	  </td>
	</tr><tr valign="top">
	  <td class="name" align="right">Relation kind</td>
	  <td><b><%= rel_kind %></b></td>
	</tr>
	
	<mm:notpresent referid="verander_ok">
		<!-- Create a relation: go to edit relation when it has > 1 editable fields (then it is a relation with a value) -->
		<mm:createrelation source="source_node" destination="dest_node" role="<%= rel_kind %>" id="the_relation">
			<mm:fieldlist type="edit"><mm:first><mm:import id="verander">ok</mm:import></mm:first></mm:fieldlist>
			<mm:field name="number" jspvar="the_rel" vartype="String" write="false">
				<% new_nr = the_rel; %>
			</mm:field>
		</mm:createrelation>
		<mm:notpresent referid="verander">
			<tr><td colspan="2"><p class="message">We've succeeded! The nodes have been related.</td></tr>
		</mm:notpresent>
	</mm:notpresent>

	<mm:present referid="verander">
		<%-- Edit relation: needed when a relation has a value (like position f.e.) --%>
		<mm:node number="<%= new_nr %>">
			<tr bgcolor="#CCCCCC"><td colspan="2" class="title-s">Edit the relation</td></tr>
			<form method="post" action="<mm:url referids="nr,rnr"><mm:param name="new_nr"><%= new_nr %></mm:param></mm:url>">
			<mm:fieldlist type="edit">
			<tr>
			  <td class="name" align="right"><mm:fieldinfo type="guiname" /></td>
			  <td><mm:fieldinfo type="input" /></td>
			</tr>
			</mm:fieldlist>
			<tr><td>&nbsp;</td><td><input type="submit" name="verander_ok" value="Save" /></td></tr>
			</form>
		</mm:node>
	</mm:present>

	<mm:present referid="verander_ok">
		<!-- Save relation -->
		<mm:node number="<%= new_nr %>">
		    <mm:fieldlist type="edit">
				 <mm:fieldinfo type="useinput" />
			</mm:fieldlist>
		</mm:node>
		<tr><td colspan="2"><p class="message">We've succeeded! The nodes have been related and your input is saved.</p></td></tr>
	</mm:present>

	</table>

</mm:present>
<%-- End mm:present rnr --%>












</mm:context>

<%@ include file="inc_foot.jsp" %>
</mm:cloud>
