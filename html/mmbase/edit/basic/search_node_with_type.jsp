<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><%@ include file="page_base_functionality.jsp"
%><mm:import externid="username" from="parameters" />
<mm:content language="$config.lang" type="text/html" expires="0">
<mm:cloud method="$config.method" loginpage="login.jsp" logon="$username" sessionname="$config.session" jspvar="cloud">
<mm:context id="context_search">
<%-- for selecting next page with listings --%>
<mm:import externid="page" vartype="integer" from="parameters">0</mm:import>

<mm:import externid="node_type"  required="true" from="parameters"/>
<mm:import externid="to_page"    required="true" from="parameters"/><!-- where to link to -->

<mm:import externid="node"       from="parameters" />

<mm:import externid="role_name"  from="parameters" />
<mm:import externid="direction"  from="parameters" /><%-- create relation dir --%>
<mm:import externid="search"     from="parameters" />
<mm:import externid="maylink"    from="parameters" />

<mm:import externid="nopush"    from="parameters" />

<mm:import externid="orderby"      from="parameters">number</mm:import>
<mm:import externid="directions"  from="parameters">DOWN</mm:import>

<!-- import search age and store in session -->

<mm:import externid="_search_form_minage_$node_type" from="parameters,session"></mm:import>
<mm:import externid="_search_form_maxage_$node_type" from="parameters,session"><mm:listnodes type="typedef" constraints="[name]='${node_type}'" max="1"><mm:field name="defaultsearchage()" /></mm:listnodes></mm:import>

<mm:write session="_search_form_minage_$node_type" referid="_search_form_minage_$node_type" />
<mm:write session="_search_form_maxage_$node_type" referid="_search_form_maxage_$node_type" />

<%-- you can configure 'hide_search' to hide the search functionality --%>
<%-- mm:compare referid="config.hide_search" value="false" --%>
<mm:context>
  <form name="search" method="post" action='<mm:url referids="node,node_type,role_name,direction" />'>
    <table class="search" align="center" width="100%" border="0" cellspacing="1">
      <%-- search table --%>
      <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
        <tr align="left">
          <td width="20%"><mm:fieldinfo type="guiname" /> <small>(<mm:fieldinfo type="name" />)</small></td>
          <td width="100%"><mm:fieldinfo type="searchinput" /></td>
        </tr>
      </mm:fieldlist>
      <tr align="left">
        <td width="20%"><%=m.getString("search.minage")%></td>
        <td width="100%"><input type ="text" class="small" size="80" name="_search_form_minage_<mm:write referid="node_type" />" value="<mm:write referid="_search_form_minage_$node_type" />" /></td>
      </tr>
      <tr align="left">
        <td width="20%"><%=m.getString("search.maxage")%></td>
        <td width="100%"><input type ="text" class="small" size="80" name="_search_form_maxage_<mm:write referid="node_type" />" value="<mm:write referid="_search_form_maxage_$node_type" />" /></td>
      </tr>
      <tr>          
        <td colspan="2"><input class="search" type="submit" name="search" value="<%=m.getString("search")%>" /></td>
      </tr>
    </table>
  </form>
</mm:context>
<%-- /mm:compare --%>


<mm:listnodescontainer type="$node_type">

<mm:present referid="search">
  <mm:context>
    <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
      <mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
    </mm:fieldlist>             
  </mm:context>
</mm:present>

<%-- apply age-constraint always --%>
<mm:ageconstraint minage="${_search_form_minage_$node_type}" maxage="${_search_form_maxage_$node_type}" />


<% boolean mayLink = false; %><mm:present referid="maylink"><% mayLink = true; %></mm:present>

 <mm:size id="totalsize" write="false" />

 <mm:offset    value="${+$page*$config.page_size}"  />
 <mm:maxnumber value="$config.page_size" />  

 <mm:write id="offset" value="${+$page*$config.page_size}" write="false" />

<mm:import id="pager">
<table><!-- pager -->
  <tr>
  <mm:context>
    <td class="navigate">
    <nobr>
    <mm:previousbatches max="20">
      <mm:first>
        <mm:index>
          <mm:compare value="0" inverse="true">
          ...
           </mm:compare>
        </mm:index>
      </mm:first>
      <a href='<mm:url referids="node,node_type,role_name,direction,search,orderby">
        <mm:param name="page"><mm:index /></mm:param>
        <!--pass all search field values -->
        <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
           <mm:fieldinfo type="reusesearchinput" />
        </mm:fieldlist>        
       </mm:url>' ><mm:index />
      <mm:last>
         <span class="previous"></span><span class="alt">[&lt;-previous page]</span>
      </mm:last>
    </a>
   </mm:previousbatches>
     </nobr>
   </td>
   </mm:context>
   <td>
   <mm:write value="$page" />
   </td>
   <mm:context>
      <td class="navigate">    
      <nobr>  
      <mm:nextbatches max="21">
       <mm:index offset="1">
       <mm:compare value="21" inverse="true">
       <a href='<mm:url referids="node,node_type,role_name,direction,orderby,search">
       <mm:param name="page"><mm:index /></mm:param>
        <!--pass all search field values -->
         <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
             <mm:fieldinfo type="reusesearchinput" />
         </mm:fieldlist>
         </mm:url>' >
         <mm:first>
        <span class="next"></span><span class="alt">[next page ->]</span>
         </mm:first>
          <mm:index />
            </a>
       </mm:compare>
       <mm:compare value="21">
        ...
       </mm:compare>
       </mm:index>  
    </mm:nextbatches>
      </nobr>
      </td>
    </mm:context>
   </tr>
</table>
</mm:import>

<mm:write referid="pager" escape="none" />


<a name="searchresult" />
<table width="100%" class="list"><!-- list table -->      
  <tr align="left"><!-- header -->
  <th>Gui()</th>
    <mm:context>
    <mm:fieldlist nodetype="$node_type" type="list" jspvar="field">
         <% if (field.hasIndex()) { %>
         <th><a href="<mm:url referids="node,node_type,role_name,direction,search">
         <mm:fieldlist id="search_form" nodetype="$node_type" type="search">
             <mm:fieldinfo type="reusesearchinput" />
         </mm:fieldlist>
         <mm:param name="orderby"><mm:fieldinfo type="name" /></mm:param>
        <mm:fieldinfo type="name">
         <mm:compare referid2="orderby">
           <mm:compare referid="directions" value="DOWN">
            <mm:param name="directions">UP</mm:param>
           </mm:compare>
         </mm:compare>
         </mm:fieldinfo>
         </mm:url>">
      <mm:fieldinfo type="guiname" /> <small>(<mm:fieldinfo type="name" />)</small></a></th>
        <% } else { %>
         <th><mm:fieldinfo type="guiname" /> <small>(<mm:fieldinfo type="name" />)</small></th>
        <% } %>
    </mm:fieldlist>
    </mm:context>
    <mm:size id="size" write="false" />
    <th colspan="2"><nobr><mm:write referid="totalsize"><mm:compare value="0"><%=m.getString("search.noresults")%></mm:compare><mm:isgreaterthan value="0"><mm:write vartype="integer" value="${+$offset + 1}" />-<mm:write vartype="integer" value="${+$offset + $size}" />/<mm:write  /></mm:isgreaterthan></mm:write></nobr></th><!-- X and -> collum -->
  </tr>

<mm:listnodes id="node_number" directions="$directions"  orderby="$orderby" jspvar="sn">
  <tr>
    <td class="listdata"><mm:nodeinfo type="gui" />&nbsp;<%-- (<mm:function name="age" />)--%></td>
   <mm:fieldlist nodetype="$node_type" type="list">
        <td class="listdata"><mm:fieldinfo type="guivalue" /> &nbsp;</td>
   </mm:fieldlist>
    <td class="navigate">
        <mm:maydelete>
          <mm:hasrelations inverse="true">
            <a href="<mm:url referids="node_type,node_number,page" page="commit_node.jsp" ><mm:param name="delete">true</mm:param></mm:url>">
              <span class="delete"></span><span class="alt">[delete]</span>
            </a>
          </mm:hasrelations>
        </mm:maydelete>
        &nbsp;
     </td>    
     <td class="navigate">  
    <% if(sn.mayWrite() || sn.mayDelete() || sn.mayChangeContext() || (mayLink)) { %>
            <a href="<mm:url page="$to_page" referids="node_number,node_number@push,nopush?" />">
                  <span class="change"></span><span class="alt">[change]</span>
            </a>
     <% } else { %>&nbsp;<% } %>
     </td>
 </tr>  
</mm:listnodes>
</table>

<mm:write referid="pager" escape="none" />

</mm:listnodescontainer>

</mm:context>
</mm:cloud>
</mm:content>