<% String title = "Home"; %>
<%@ include file="inc_top.jsp" %>
<mm:cloud name="mmbase" jspvar="wolk" method="http" rank="basic user">
<mm:import externid="ntype" jspvar="ntype" />
<mm:import externid="search" />
<mm:import externid="s" />
<% String path1 = ntype;		// Eerst stukje van kruimelpad %>
<%@ include file="inc_head.jsp" %>

<table border="0" cellspacing="0" cellpadding="3">
<tr>
<td valign="top" width="20%">
<!-- Choose a node type -->
<table border="0" cellspacing="0" cellpadding="3">
<% // Choose a node type
String guiName = "";
NodeManagerList l = wolk.getNodeManagers();
java.util.Collections.sort(l);
for (int i = 0; i < l.size(); i++) {
	NodeManager nm = l.getNodeManager(i);
	guiName = nm.getGUIName();
%>
	<tr>
	  <td align="right"><b><%= nm.getName() %>:</b></td>
	  <td><a href="index.jsp?ntype=<%= nm.getName() %>" title="show nodes"><%= guiName %></a></td>
	  <td nowrap="nowrap">
	    <a href="index.jsp?ntype=<%= nm.getName() %>" title="show recent nodes"><img src="img/mmbase-search.gif" alt="show recent nodes" width="21" height="20" border="0" /></a>
	    <a href="new_object.jsp?ntype=<%= nm.getName() %>" title="new node"><img src="img/mmbase-new.gif" alt="new node" width="21" height="20" border="0" /></a>
	  </td>
	</tr>
<% } %>
</table>
<!-- Einde nodetypes -->
</td>
<td valign="top" width="80%">

<!-- ### Start search and show (include?) ### -->
<!-- Action: we do allways need a node type -->
<mm:present referid="ntype">

<mm:present referid="search">
	<%-- Create a search string to use as constraint on a list --%>
	<mm:import id="search_string"><mm:context><mm:fieldlist nodetype="$ntype" type="search"><mm:fieldinfo type="usesearchinput"><mm:isnotempty><mm:present referid="notfirst"> AND </mm:present><mm:notpresent referid="notfirst"><mm:import id="notfirst">yes</mm:import></mm:notpresent><mm:write /></mm:isnotempty></mm:fieldinfo></mm:fieldlist></mm:context></mm:import>
</mm:present>
<mm:notpresent referid="search">
	<mm:import id="search_string"><mm:write referid="s" /></mm:import>
</mm:notpresent>
<mm:import jspvar="search_str"><mm:write referid="search_string" /></mm:import>

<% // Constraints in case of an empty searchstring (will get all the nodes of this type)
// We will try to use a day offset
// Get day of today in days since 01/01/1970
Date today = new Date();
int days_ofs = Integer.parseInt(conf_days);
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


<!-- ### Show results ### -->
<% // Total found
int total_found = 0;
int colspan = 2;		// Colspan for navigation buttons below 
int list_index = 0;		// Index value of the list
%>
<mm:listnodes type="$ntype" constraints="<%= search_str %>">
<mm:first>
	<mm:size jspvar="list_size" vartype="Integer" write="false"><% total_found = list_size.intValue(); %></mm:size>
</mm:first>
</mm:listnodes>

<!-- zoekstring voor node_type met daymarker: <%= search_str %> -->
<mm:fieldlist type="list" nodetype="$ntype"><% colspan++; %></mm:fieldlist>
<table width="100%" border="0" cellspacing="0" cellpadding="4">
<tr bgcolor="#CCCCCC">
  <td colspan="<%= colspan - 1 %>" class="title-m">
	<%= total_found %> found of type 
	<mm:nodeinfo nodetype="$ntype" type="guitype" />  (<%= ntype %>) 
  </td>
  <td>
	<a href="#search" title="search"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a>
	<a href="new_object.jsp?ntype=<%= ntype %>" title="new"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a>
 </td>
</tr>
<mm:listnodes id="node_number" type="$ntype" 
	directions="DOWN" orderby="number"
	max="<%= conf_max %>" offset="<%= ofs_str %>"
	constraints="<%= search_str %>">
	<mm:first>
	<tr>
	  <td>&nbsp;</td>
	  <mm:fieldlist type="list" nodetype="$ntype"><td class="name"> <mm:fieldinfo type="guiname" /> </td></mm:fieldlist>
	  <td>&nbsp;</td>
	</tr>
<!-- result rows -->
	</mm:first>
	<tr valign="top"<mm:odd> bgcolor="#EFEFEF"</mm:odd>> 
  	  <td align="right">
  	    <mm:index jspvar="index_str" vartype="String" write="false"><% list_index = ofs + Integer.parseInt(index_str); %></mm:index>
  	    <%= list_index %>
  	  </td>
  	  <% int i = 0; %>
	  <mm:fieldlist type="list"><td><% if (i==0) { %><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"></mm:maywrite><% } %><mm:fieldinfo type="guivalue" /><% if (i==0) { %><mm:maywrite></a></mm:maywrite><% } %> </td> <% i++; %></mm:fieldlist>
  	  <td nowrap="nowrap">
  	    <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></mm:maywrite>
		<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
  	  </td>
	</tr>
	<mm:last>
<!-- result rows end -->
	<tr bgcolor="#FFFFFF">
	  <td colspan="<%= colspan %>" align="center">
	<% // Calculation prev, next
	int next = ofs + max;
	int prev = ofs - max;
	if (prev >= -1) { if (prev < 0 ) { prev = 0; }
	%>
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= prev %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	<% 
	}
	if (ofs >= max) {  
	%>
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param value="0" name="o" /><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" border="0" /></a>
	<%
	}
	if (next < total_found) { 
	%>
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= next %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	<% } %>
	  </td>
	</tr></table>
	</mm:last>
</mm:listnodes>

<!-- ### Search form ### -->
<p><a name="search">&nbsp;</a></p>
<form method="post" action="<mm:url referids="ntype" />">
<table border="0" cellspacing="0" cellpadding="4" class="table-form">
	<tr bgcolor="#CCCCCC">
	  <td colspan="2" class="title-m">Search node of type <mm:nodeinfo nodetype="$ntype" type="guitype" /> (<%= ntype %>)</td>
	</tr>
	<tr valign="top">
	  <td align="right" class="name">Days old</td>
	  <td><input type="text" name="conf_days" value="<%= conf_days %>" size="9" maxlength="9"></td>
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
<!-- ### End Search include? ### -->

</td>
</tr>
</table>
<%@ include file="inc_foot.jsp" %>
</mm:cloud>
