<mm:context id="context_search">
<!-- for selecting next page with listings -->
<mm:import externid="page" vartype="decimal" from="parameters">0</mm:import>

<mm:import externid="node_type"  required="true" from="parent"/>
<mm:import externid="to_page"    required="true" from="parent"/><!-- where to link to -->

<mm:import externid="node"       from="parent" />

<mm:import externid="role_name"  from="parameters" />
<mm:import externid="search"     from="parameters" />
<mm:import externid="maylink"    from="parameters" />

<!-- you can configure 'hide_search' to hide the search functionality -->    
<mm:compare referid="config.hide_search" value="false">
<mm:context>
  <form name="search" method="post" action='<mm:url referids="node,node_type,role_name" />'>
      <table class="search" width="100%" border="0" cellspacing="1">
        <!-- search table -->
        <mm:fieldlist id="search_form" nodetype="${node_type}" type="search">
            <tr align="left">
             <td><mm:fieldinfo type="guiname" /> <small>(<mm:fieldinfo type="name" />)</small></td>
             <td><mm:fieldinfo type="searchinput" /></td>
           </tr>
        </mm:fieldlist>
        <tr>
           <td colspan="2"><input class="search" type ="submit" name="search" value="search" /></td>
        </tr>
      </table>
  </form>
</mm:context>
</mm:compare>

<mm:notpresent referid="search">
    <mm:import id="where" />
</mm:notpresent>

<!-- ordered to search with form button 'search'. Following are some tricks to get the where right.-->    
<mm:present referid="search">
  <mm:import id="where"><mm:context><mm:fieldlist id="search_form" nodetype="$node_type" type="search"><mm:fieldinfo type="usesearchinput"><mm:isnotempty><mm:present referid="notfirst"> AND </mm:present><mm:notpresent referid="notfirst"><mm:import id="notfirst">yes</mm:import></mm:notpresent><mm:write /></mm:isnotempty></mm:fieldinfo></mm:fieldlist></mm:context></mm:import>
    <!-- -everything on one line, to avoid pollution of $where with spaces.
         -context is used to avoid pollution of current context with used id's. --> 
</mm:present>

             
<% boolean mayLink = false; %><mm:present referid="maylink"><% mayLink = true; %></mm:present>

<a name="searchresult" />
<table width="100%" border="0"><!-- list table -->      
  <tr align="left"><!-- header -->
    <mm:fieldlist nodetype="$node_type" type="list">
         <th><mm:fieldinfo type="guiname" /> <small>(<mm:fieldinfo type="name" />)</small></th>
    </mm:fieldlist>
    <th>&nbsp;</th><!-- X collum -->
    <th>&nbsp;</th><!-- -> collum -->
  </tr>
  <mm:listnodes id="node_number" type="$node_type" directions="DOWN"   orderby="number"
                offset="${+$page*$config.page_size}"  max="$config.page_size"
                jspvar="sn"
                constraints="$where">
  <tr>
  <mm:fieldlist type="list">
        <td class="listdata"><mm:fieldinfo type="guivalue" />&nbsp;</td>
   </mm:fieldlist>
    <td class="navigate">
        <mm:maydelete>
          <mm:countrelations>
            <mm:compare value="0">
              <a href="<mm:url referids="node_type,node_number,page" page="commit_node.jsp" ><mm:param name="delete">true</mm:param></mm:url>">
                            <img src="images/delete.gif" alt="[delete]" width="20" height="20" border="0" align="right"/>
              </a>
            </mm:compare>
          </mm:countrelations>
        </mm:maydelete>
        &nbsp;
     </td>    
     <td class="navigate">  
    <% if(sn.mayWrite() || sn.mayDelete() || sn.mayChangeContext() || (mayLink)) { %>
            <a href="<mm:url page="$to_page" referids="node_number" />">
                      <img src="images/select.gif" alt="[change]" width="20" height="20" border="0" align="right"/>
            </a>
     <% } else { %>&nbsp;<% } %>
     </td>
 </tr>  
 <mm:last>
   <mm:index>
      <mm:compare referid2="config.page_size">
         <mm:import id="next_page">jes</mm:import>
      </mm:compare>
  </mm:index>
</mm:last>
</mm:listnodes>
</table>

<table><!-- pager -->
  <tr>
    <mm:isgreaterthan referid="page" value="0.5">
      <td class="navigate">
            <a href='<mm:url referids="node,node_type,role_name,search"><mm:param name="page" value="${+$page-1}" /></mm:url>' >
        <img src="images/previous.gif" alt="[<-previous page]" width="20" height="20" border="0" />
      </a>
        </td> 
    </mm:isgreaterthan>
    <mm:present referid="next_page">
      <td class="navigate">      
            <a href='<mm:url referids="node,node_type,role_name,search"><mm:param name="page"  value="${+$page+1}" /></mm:url>' >
              <img src="images/next.gif" alt="[next page->]" width="20" height="20" border="0" align="right"/>
            </a>
      </td>
    </mm:present>
   </tr>
</table>
</mm:context>
