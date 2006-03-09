<mm:present referid="ntype">
<mm:import externid="search" />
<mm:listnodescontainer type="$ntype">
  <% 
  int span = 0;         // # of fields
  NodeManager nm = wolk.getNodeManager(ntype);
  %>
  <mm:size id="totsize" write="false" />
  <mm:ageconstraint minage="0" maxage="$conf_days" />
  <mm:present referid="search">
    <mm:context>
      <mm:fieldlist nodetype="$ntype" type="search">
        <mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
      </mm:fieldlist>             
    </mm:context>
  </mm:present>
  
  <%-- calculating totsize after a search --%>
  <mm:size write="false" id="size" />
  <%-- calculate # fields --%>
  <mm:fieldlist type="list" nodetype="$ntype"><% span++; %></mm:fieldlist>
  <table width="100%" border="0" cellspacing="0" cellpadding="4" class="table-results">
  <tr bgcolor="#CCCCCC">
    <td>&nbsp;</td>
    <td colspan="<%= span + 1 %>" class="title-s">
      <mm:write referid="size" /> out of <mm:write referid="totsize" /> 
      of type <b><mm:nodeinfo nodetype="$ntype" type="guitype" /></b>  (<mm:write referid="ntype" />) 
    </td>
    <td align="right" nowrap="nowrap">
      <a href="#search" title="search"><img src="img/mmbase-search.gif" alt="search" width="21" height="20" border="0" /></a>
      <% if (nm.mayCreateNode()) { %><a href="new_object.jsp?ntype=<mm:write referid="ntype" />" title="new"><img src="img/mmbase-new.gif" alt="new" width="21" height="20" border="0" /></a><% } %>
   </td>
  </tr>
  <mm:listnodes id="node_number"
    max="$conf_max" offset="$ofs"
    directions="DOWN" orderby="number">
    <mm:compare referid="totsize" value="0">
      <mm:import reset="true" id="totsize"><mm:write referid="size" /></mm:import>
    </mm:compare>
    <mm:first>
    <!-- table with search results -->
    <tr> <!-- fieldlist with fieldnames -->
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <mm:fieldlist type="list" nodetype="$ntype"><td class="name"> <mm:fieldinfo type="guiname" /> </td></mm:fieldlist>
      <td>&nbsp;</td>
    </tr> <!-- start of fieldlist with the values -->
    </mm:first>
    <tr valign="top"<mm:odd> bgcolor="#EFEFEF"</mm:odd>> 
      <td align="center">
      <mm:present referid="nr"><%-- if there is a nr, there is a node and thus we are trying to find another to relate to --%>
        <mm:field name="number"><mm:compare value="$nr" inverse="true"><%-- don't relate to self --%>
        <mm:compare referid="dir" value="nwchild"><mm:maycreaterelation role="$rkind" source="nr" destination="node_number">
          <a title="relate" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="img/mmbase-relation-right.gif" alt="-&gt;" width="21" height="20" border="0" /></a>
        </mm:maycreaterelation></mm:compare>
        <mm:compare referid="dir" value="nwparent"><mm:maycreaterelation role="$rkind" source="node_number" destination="nr">
          <a title="relate" href="<mm:url page="relate_object.jsp" referids="ntype,nr,rkind,dir"><mm:param name="rnr"><mm:field name="number" /></mm:param></mm:url>"><img src="img/mmbase-relation-left.gif" alt="-&lt;" width="21" height="20" border="0" /></a>
        </mm:maycreaterelation></mm:compare>
        </mm:compare></mm:field>
      </mm:present>
      </td>
      <td align="right"><% String o = String.valueOf(ofs.intValue() + 1); %><mm:index offset="<%= o %>" /><%-- mm:index offset="$[+$ofs + 1]" / --%></td>
      <% int i = 0; // to check if we should make a link %>
      <mm:fieldlist type="list" nodetype="$ntype">
        <td><% if (i==0) { %><mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit"></mm:maywrite><% } %><mm:fieldinfo type="guivalue" /><% if (i==0) { %><mm:maywrite></a></mm:maywrite><% } %> </td>
      <% i++; %></mm:fieldlist>
      <td nowrap="nowrap" align="right">
        <mm:maywrite><a href="edit_object.jsp?nr=<mm:field name="number" />" title="edit node"><img src="img/mmbase-edit.gif" alt="edit" width="21" height="20" border="0" /></a></mm:maywrite>
        <mm:maydelete><a href="delete_object.jsp?nr=<mm:field name="number" />" title="delete node"><img src="img/mmbase-delete.gif" alt="delete" width="21" height="20" border="0" /></a></mm:maydelete>
      </td>
    </tr> <mm:remove referid="relnr" />
  </mm:listnodes>
  <%-- pass all search field values --%>
  <mm:url id="search_str" referids="nr?,conf_days,ntype,search" write="false">
    <mm:fieldlist nodetype="$ntype" type="search">
      <mm:fieldinfo type="reusesearchinput" />
    </mm:fieldlist>
  </mm:url>
  <tr bgcolor="#FFFFFF">
    <td class="title-s" colspan="<%= span + 3 %>" align="center"> &nbsp;
    <mm:present referid="search"><mm:compare referid="size" value="0">Nothing found.</mm:compare></mm:present>
    <%-- paging --%>
    <mm:compare referid="size" value="0" inverse="true">
      <mm:previousbatches maxtotal="20" indexoffset="1">
        <mm:first><mm:index><mm:compare value="1" inverse="true">&laquo;&laquo;&nbsp;</mm:compare></mm:index></mm:first>
        <a href="<mm:url referid="search_str" referids="_@ofs" />"><mm:index /></a> |
      </mm:previousbatches>
      <mm:index offset="1" />
      <mm:nextbatches maxtotal="20" indexoffset="1">
        <mm:first>|</mm:first>
        <a href="<mm:url referid="search_str" referids="_@ofs" />"><mm:index /></a>
        <mm:last>
          <mm:write><mm:islessthan value="$[+ $totsize - $conf_max]">&nbsp;&raquo;&raquo;</mm:islessthan></mm:write>
        </mm:last>
        <mm:last inverse="true">|</mm:last>
      </mm:nextbatches>
      <%-- /paging --%>
    </mm:compare>
    </td>
  </tr>
  </table>
  <!-- /table with search results -->
</mm:listnodescontainer>
</mm:present>
