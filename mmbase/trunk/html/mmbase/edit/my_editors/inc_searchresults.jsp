<mm:present referid="ntype">
<mm:import externid="search" />
<mm:import externid="s" />

<mm:present referid="search">	<%-- Create a search string to use as constraint on a list --%>
	<mm:import id="search_string"><mm:context><mm:fieldlist nodetype="$ntype" type="search"><mm:fieldinfo type="usesearchinput"><mm:isnotempty><mm:present referid="notfirst"> AND </mm:present><mm:notpresent referid="notfirst"><mm:import id="notfirst">yes</mm:import></mm:notpresent><mm:write /></mm:isnotempty></mm:fieldinfo></mm:fieldlist></mm:context></mm:import>
</mm:present>
<mm:notpresent referid="search"><mm:import id="search_string"><mm:write referid="s" /></mm:import></mm:notpresent>

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
NodeManager nm = wolk.getNodeManager(ntype);
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

// ### The results using the search_str ###
// Total found
int total_found = 0;
int colspan = 2;		// Colspan for navigation buttons below 
int list_index = 0;		// Index value of the list
%>
<mm:listnodes type="$ntype" constraints="<%= search_str %>">
	<mm:first>
		<mm:size jspvar="list_size" vartype="Integer" write="false"><% total_found = list_size.intValue(); %></mm:size>
	</mm:first>
</mm:listnodes>
<!-- search_str for node_type with daymarker: <%= search_str %> -->
<mm:fieldlist type="list" nodetype="$ntype"><% colspan++; %></mm:fieldlist>
<table width="100%" border="0" cellspacing="0" cellpadding="4">
<tr bgcolor="#CCCCCC">
  <td align="right" class="title-s" colspan="2"><%= total_found %></td>
  <td colspan="<%= colspan - 2 %>" class="title-s">	
  	found of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b>  (<mm:write referid="ntype" />) 
  </td>
  <td align="right" nowrap="nowrap">
	<a href="#search" title="search"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a>
	<% if (nm.mayCreateNode()) { // may create node of this type? %><a href="new_object.jsp?ntype=<mm:write referid="ntype" />" title="new"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a><% } %>
 </td>
</tr>
<mm:listnodes id="node_number" type="$ntype" 
	directions="DOWN" orderby="number"
	max="<%= conf_max %>" offset="<%= ofs_str %>"
	constraints="<%= search_str %>">
	<mm:first>
	<tr>
	  <td>&nbsp;</td>
	  <td>&nbsp;</td>
	  <mm:fieldlist type="list" nodetype="$ntype"><td class="name"> <mm:fieldinfo type="guiname" /> </td></mm:fieldlist>
	  <td>&nbsp;</td>
	</tr>
<!-- results -->
	</mm:first>
	<tr valign="top"<mm:odd> bgcolor="#EFEFEF"</mm:odd>> 
	  <td align="center">
	  <mm:present referid="nr">		<%-- if there is a nr, there is a node and thus we are trying to find another to relate to --%>
		<mm:compare referid="dir" value="nwchild"><mm:maycreaterelation role="$rkind" source="nr" destination="node_number">
		  <a title="relate" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="img/mmbase-relation-right.gif" alt="-&gt;" width="21" height="20" border="0" /></a>
		</mm:maycreaterelation></mm:compare>
		<mm:compare referid="dir" value="nwparent"><mm:maycreaterelation role="$rkind" source="node_number" destination="nr">
		  <a title="relate" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="img/mmbase-relation-left.gif" alt="-&lt;" width="21" height="20" border="0" /></a>
		</mm:maycreaterelation></mm:compare>
	  </mm:present>
	  </td>
  	  <td align="right">
  	    <mm:index jspvar="index_str" vartype="String" write="false"><% list_index = ofs + Integer.parseInt(index_str); %></mm:index>
  	    <%= list_index %>
  	  </td>
  	  <% int i = 0; %>
	  <mm:fieldlist type="list" nodetype="$ntype"><td><% if (i==0) { %><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"></mm:maywrite><% } %><mm:fieldinfo type="guivalue" /><% if (i==0) { %><mm:maywrite></a></mm:maywrite><% } %> </td> <% i++; %></mm:fieldlist>
  	  <td nowrap="nowrap" align="right">
  	    <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></mm:maywrite>
		<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete node"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
  	  </td>
	</tr> <mm:remove referid="relnr" />
	<mm:last>
<!-- /results -->
	<tr bgcolor="#FFFFFF">
	  <td colspan="<%= colspan + 1 %>" align="center">
	<% // Calculation prev, next
	int next = ofs + max;
	int prev = ofs - max;
	if (prev >= -1) { if (prev < 0 ) { prev = 0; }
	%>
	<mm:present referid="nr">
		<a href="<mm:url referids="ntype,nr,rkind,dir"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= prev %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= prev %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	</mm:notpresent>
	<% 
	}
	if (ofs >= max) {  
	%>
	<mm:present referid="nr">
		<a href="<mm:url referids="ntype,nr,rkind"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param value="0" name="o" /><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param value="0" name="o" /><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" border="0" /></a>
	</mm:notpresent>
	<%
	}
	if (next < total_found) { 
	%>
	<mm:present referid="nr">
		<a href="<mm:url referids="ntype,nr,rkind,dir"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= next %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referids="ntype"><mm:param name="conf_days"><%= conf_days %></mm:param><mm:param name="o"><%= next %></mm:param><mm:param name="s" value="$search_string" /></mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	</mm:notpresent>
	<% } %>
	  </td>
	</tr>
	</mm:last>
</mm:listnodes>
</table>

</mm:present>
