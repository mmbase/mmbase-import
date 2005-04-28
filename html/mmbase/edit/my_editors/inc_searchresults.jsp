<mm:present referid="ntype">
<mm:import externid="search" />
<mm:listnodescontainer type="$ntype">
  <% 
  int span = 0;			// # of fields
  int tot_found = 0;	
  int list_index = 0;		// Index value of the list
  NodeManager nm = wolk.getNodeManager(ntype);
  %>
  <mm:size id="totsize" write="false" jspvar="l_size" vartype="Integer"><% tot_found = l_size.intValue(); %></mm:size>
  <mm:ageconstraint minage="0" maxage="$conf_days" />
  <mm:present referid="search">
	<mm:context>
	  <mm:fieldlist nodetype="$ntype" type="search">
		<mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
	  </mm:fieldlist>             
	</mm:context>
  	<mm:size write="false" jspvar="l_size" vartype="Integer"><% tot_found = l_size.intValue(); %></mm:size>
  </mm:present>
  <%-- calculate # fields --%>
  <mm:fieldlist type="list" nodetype="$ntype"><% span++; %></mm:fieldlist>
  <mm:listnodes	id="node_number"
  	max="<%= conf_max %>" offset="<%= ofs_str %>"
  	directions="DOWN" orderby="number">
  	<% if (tot_found == 0) { %><mm:size write="false" jspvar="l_size" vartype="Integer"><% tot_found = l_size.intValue(); %></mm:size><%}%>
	<mm:first>
	<!-- table with search results -->
	<table width="100%" border="0" cellspacing="0" cellpadding="4" class="table-results">
	<tr bgcolor="#CCCCCC">
	  <td colspan="<%= span + 2 %>" class="title-s">
		<%= tot_found %> out of <mm:write referid="totsize" /> of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b>  (<mm:write referid="ntype" />) 
	  </td>
	  <td align="right" nowrap="nowrap">
		<a href="#search" title="search"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a>
		<% if (nm.mayCreateNode()) { // may create node of this type? %><a href="new_object.jsp?ntype=<mm:write referid="ntype" />" title="new"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a><% } %>
	 </td>
	</tr>
	<tr> <!-- fieldlist with fieldnames -->
	  <td>&nbsp;</td>
	  <td>&nbsp;</td>
	  <mm:fieldlist type="list" nodetype="$ntype"><td class="name"> <mm:fieldinfo type="guiname" /> </td></mm:fieldlist>
	  <td>&nbsp;</td>
	</tr> <!-- start of fieldlist with the values -->
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
  	  <% int i = 0; // to check if we should make a link %>
	  <mm:fieldlist type="list" nodetype="$ntype"><td><% if (i==0) { %><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"></mm:maywrite><% } %><mm:fieldinfo type="guivalue" /><% if (i==0) { %><mm:maywrite></a></mm:maywrite><% } %> </td> <% i++; %></mm:fieldlist>
  	  <td nowrap="nowrap" align="right">
  	    <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></mm:maywrite>
		<mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete node"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
  	  </td>
	</tr> <mm:remove referid="relnr" />
    <mm:last>
	<%-- pass all search field values --%>
	<mm:url id="search_str" referids="conf_days,ntype,search" write="false">
	  <mm:fieldlist nodetype="$ntype" type="search">
		<mm:fieldinfo type="reusesearchinput" />
	  </mm:fieldlist>
	</mm:url>
	<tr bgcolor="#FFFFFF">
	  <td colspan="<%= span + 3 %>" align="center">
	<% // Calculation prev, next
	int next = ofs + max;
	int prev = ofs - max;
	if (prev >= -1) { if (prev < 0 ) { prev = 0; }
	%>
	<mm:present referid="nr">
		<a href="<mm:url referid="search_str" referids="nr,rkind,dir">
			<mm:param name="o"><%= prev %></mm:param>
		</mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referid="search_str">
			<mm:param name="o"><%= prev %></mm:param>
		</mm:url>"><img src="img/mmbase-left.gif" alt="previous" width="21" height="20" border="0" /></a>
	</mm:notpresent>
	<% 
	}
	if (ofs >= max) {  
	%>
	<mm:present referid="nr">
		<a href="<mm:url referid="search_str" referids="nr,rkind,dir">
			<mm:param name="o" value="0" />
		</mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" hspace="5" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referid="search_str">
			<mm:param name="o" value="0" />
		</mm:url>"><img src="img/mmbase-up.gif" alt="index" width="21" height="20" hspace="5" border="0" /></a>
	</mm:notpresent>
	<%
	}
	if (next < tot_found) { 
	%>
	<mm:present referid="nr">
		<a href="<mm:url referid="search_str" referids="nr,rkind,dir">
			<mm:param name="o"><%= next %></mm:param>
		</mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	</mm:present>
	<mm:notpresent referid="nr">
		<a href="<mm:url referid="search_str">
			<mm:param name="o"><%= next %></mm:param>
		</mm:url>"><img src="img/mmbase-right.gif" alt="next" width="21" height="20" border="0" /></a>
	</mm:notpresent>
	<% } %>
	  </td>
	</tr>
    </table>
    </mm:last>
  </mm:listnodes>
  <!-- /table with search results -->
</mm:listnodescontainer>
</mm:present>
