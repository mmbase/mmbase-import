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
<tr>
    <td class="data">
        <%=otherManager.getGUIName()%> (<%=guirole%>)
    </td>
    <th colspan="3"><%=m.getString("relations.relations")%></th>
    <th colspan="3"><%=m.getString("relations.related")%></th>
    <td class="navigate">
        <%-- <%= m.getString("new_relation.new")%> --%>
        <a href='<mm:url page="new_relation.jsp" >
            <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
            <mm:param name="node_type"><%= otherManager.getName()%></mm:param>
            <mm:param name="role_name"><%= role %></mm:param>
            <mm:param name="direction"><%= searchDir.equals("destination") ? "create_child" : "create_parent" %></mm:param>
            </mm:url>'>
           <span class="create"></span><span class="alt">+</span>
       </a>
    </td>
</tr>

<mm:listrelations role="<%=role%>" type="<%=otherManager.getName()%>" searchdir="<%=searchDir%>">
<tr>
  <%-- skip first field --%>
  <td>&nbsp;</td>
  <td class="data">
    #<mm:field id="node_number"  name="number" />
  </td>
  <td class="data">
    <mm:nodeinfo type="gui" />  (<mm:field name="owner" />)
  </td>
  <td class="navigate">
    <mm:maydelete>
      <%-- delete the relation node, not sure about the node_type argument! --%>
      <a href='<mm:url page="commit_node.jsp">
        <mm:param name="node_number"><mm:field name="number" /></mm:param>
        <mm:param name="node_type"><mm:nodeinfo type="nodemanager" /></mm:param>
        <mm:param name="delete">true</mm:param>
        </mm:url>' >
        <span class="delete"></span><span class="alt">x</span>
      </a>
    </mm:maydelete>
    <mm:maywrite>
      <%-- edit the relation --%>
      <a href="<mm:url referids="node_number,node_number@push" page="change_node.jsp" />" >
        <span class="select"></span><span class="alt">-&gt;</span>
      </a>
    </mm:maywrite>
  </td>
  <mm:relatednode>
    <td class="data">
      #<mm:field id="relatednumber" name="number" />
    </td>
    <td class="data">
      <mm:nodeinfo type="gui" /> (<mm:field name="owner" />)
    </td>
    <td class="navigate">
       <%-- edit the related node --%>
      <a href="<mm:url referids="relatednumber@node_number,relatednumber@push" page="change_node.jsp" />">
      <span class="select"></span><span class="alt">-&gt;</span>
    </a>
  </td>
</mm:relatednode>
<%-- skip last field --%>
<td>&nbsp;</td>
</tr>
</mm:listrelations>
</mm:context>
<%
} // while
%>

