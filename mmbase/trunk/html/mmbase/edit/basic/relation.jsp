<%--
 Create tr-'s representing the relations
--%>
 <% RelationManagerIterator relIterator = node.getNodeManager().getAllowedRelations((NodeManager) null, null, searchDir).relationManagerIterator();
   while(relIterator.hasNext()) {
      RelationManager relationManager = relIterator.nextRelationManager();
      NodeManager otherManager;
      try {
         otherManager = searchDir.equals("destination") ? relationManager.getDestinationManager() : relationManager.getSourceManager();
      } catch (NotFoundException e) {
        continue;
      }

      String      role         =  relationManager.getForwardRole();
      String      guirole      =  searchDir.equals("destination") ? relationManager.getForwardGUIName()     : relationManager.getReciprocalGUIName();
      %>

<mm:context>
<mm:listrelationscontainer role="<%=role%>" type="<%=otherManager.getName()%>" searchdir="<%=searchDir%>">
<mm:size id="totalsize" write="false" />

<mm:write id="externpageid" write="false" value='<%="page" + relationManager.getNumber() + searchDir%>' />

<mm:import id="page" externid="$externpageid">0</mm:import>

<mm:write id="offset" value="$[+ $page * $config.page_size]" write="false" vartype="integer" />
<mm:maxnumber value="$config.page_size" />
<mm:offset    value="$offset" />


<tr>
    <td class="data">
        <%=otherManager.getGUIName()%>
        (<%= role %>: <%=guirole%>)
        <span class="nmname"><%=otherManager.getName()%></span>
    </td>
    <th colspan="3"><%=m.getString("relations.relations")%></th>
    <th colspan="3"><%=m.getString("relations.related")%>
    </th>
    <th>
      <a href='<mm:url page="new_relation.jsp" >
            <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
            <mm:param name="node_type"><%= otherManager.getName()%></mm:param>
            <mm:param name="role_name"><%= role %></mm:param>
            <mm:param name="direction"><%= searchDir.equals("destination") ? "create_child" : "create_parent" %></mm:param>
            </mm:url>'>
           <span class="create"><!-- needed for IE --></span><span class="alt">+</span>
       </a>
      <mm:previousbatches max="1">
        <a href='<mm:url referid="purl">
        <mm:param name="$externpageid"><mm:index /></mm:param>
        </mm:url>' >
        <span class="previous"></span><span class="alt">[&lt;-previous page]</span>
        </a>
      </mm:previousbatches>
      <mm:size id="size">
        <mm:isgreaterthan value="0">
           <mm:write vartype="integer" value="$[+$offset + 1]" />-<mm:write vartype="integer" value="$[+$offset+$size]" />/<mm:write referid="totalsize"  />
        </mm:isgreaterthan>
      </mm:size>
      <mm:nextbatches max="1">
        <a href='<mm:url referid="purl">
        <mm:param name="$externpageid"><mm:index /></mm:param>
        </mm:url>' >
        <span class="next"></span><span class="alt">[next page -&gt;]</span>
      </mm:nextbatches>

      </th>
</tr>



<mm:listrelations>
<tr>
  <%-- skip first field --%>
  <td>&nbsp;</td>
  <td class="data">
    #<mm:field id="node_number"  name="number" />
  </td>
  <td
     <%@include file="node_title.jsp" %>
     class="data">
    <mm:nodeinfo type="gui" />  (<mm:field name="owner" />)
  </td>
  <td class="navigate">
    <mm:maydelete>
      <%-- delete the relation node, not sure about the node_type argument! --%>
      <mm:countrelations>
        <mm:isgreaterthan value="0">(${_} ${_ eq 1 ? 'relation' : 'relations'})</mm:isgreaterthan>
        <a href='<mm:url page="commit_node.jsp">
          <mm:param name="node_number"><mm:field name="number" /></mm:param>
          <mm:param name="node_type"><mm:nodeinfo type="nodemanager" /></mm:param>
          <mm:param name="deleterelations">true</mm:param>
          </mm:url>'
          onclick="${_ eq 0 ? '' : 'return confirm(\'Are you sure?\');'}"
          >
          <span class="delete"><!-- needed for IE --></span><span class="alt">x</span>
        </a>
      </mm:countrelations>
    </mm:maydelete>
    <mm:maywrite>
      <%-- edit the relation --%>
      <a href="<mm:url referids="node_number,node_number@push" page="change_node.jsp" />" >
        <span class="select"><!-- needed for IE --></span><span class="alt">-&gt;</span>
      </a>
    </mm:maywrite>
  </td>
  <mm:relatednode>
    <td class="data">
      #<mm:field id="relatednumber" name="number" />
    </td>
    <td
     <%@include file="node_title.jsp" %>
     class="data" colspan="2">
      <mm:nodeinfo type="gui" /> (<mm:field name="owner" />)
    </td>
    <td class="navigate" colspan="1">
       <%-- edit the related node --%>
      <a href="<mm:url referids="relatednumber@node_number,relatednumber@push" page="change_node.jsp" />">
      <span class="select"><!-- needed for IE --></span><span class="alt">-&gt;</span>
    </a>
  </td>
</mm:relatednode>
</tr>
</mm:listrelations>
</mm:listrelationscontainer>
</mm:context>
<%
} // while
%>

