<table border="0" cellspacing="0" cellpadding="3" id="relations<%= searchdir %>">
<caption>Related nodes: <strong><%= searchdir %></strong></caption>
<thead>
<tr>
  <th scope="col">edit</th>
  <th scope="col">nodes</th>
  <th scope="col">create &amp; search or relate</th>
</tr>
</thead>
<tbody>
<%
// ## Parent relations ##
// Get the allowed relations of this type of node (when this node is the destination node)
relIterator = nm.getAllowedRelations( (NodeManager) null, null, searchdir).relationManagerIterator();
c = 0;  // lay-out counter
while(relIterator.hasNext()) {
    RelationManager relationManager = relIterator.nextRelationManager();
    String family = "";
    String arrow = "";
    
    NodeManager otherManager;
    if (searchdir.equals("source")) {
        try {
            otherManager = relationManager.getSourceManager();
        } catch (NotFoundException e) {
            continue;
        }
        family = "Parent";
        arrow = "relright";
    } else {
        try {
            otherManager = relationManager.getDestinationManager();
        } catch (NotFoundException e) {
            continue;
        }
        family = "Child";
        arrow = "relleft";
    }
    
    c++;
    
    String role  = relationManager.getForwardRole();
    String otherManagerName = otherManager.getName();
    int relations = node.countRelatedNodes(otherManagerName);
    String maxRelations = relationManager.getStringValue("max");    // max nr. of relation
    
%>
    <tr>
      <td class="left">&nbsp;</td>
      <td><strong><%= otherManager.getGUIName()%></strong> <%= otherManagerName %><br /><%= role %></td>
      <td class="buttons">
        <a title="create new node of type <%= otherManagerName %>" href="<mm:url page="new_object.jsp" referids="nr">
          <mm:param name="ntype"><%= otherManagerName %></mm:param>
          <mm:param name="rkind"><%= role %></mm:param>
          <mm:param name="dir"><%= searchdir %></mm:param>
        </mm:url>"><img src="img/mmbase-new.png" alt="new" width="21" height="20" /></a>
        <a title="search node of type <%= otherManagerName %> to relate to" href="<mm:url page="relate_object.jsp" referids="nr">
          <mm:param name="ntype"><%= otherManagerName %></mm:param>
          <mm:param name="rkind"><%= role %></mm:param>
          <mm:param name="dir"><%= searchdir %></mm:param>
        </mm:url>"><img src="img/mmbase-search.png" alt="search" width="21" height="20" /></a>
      </td>
    </tr>
    <mm:listrelationscontainer type="<%= otherManagerName %>" role="<%= role %>" searchdir="$searchdir">
    <mm:maxnumber value="$max" />
    <mm:offset value="$offset" />
    <mm:import id="size" reset="true"><mm:size /></mm:import>
    <mm:listrelations>
      <mm:context>
      <mm:import id="relation" reset="true"><mm:field name="number" /></mm:import>
      <mm:relatednode>
      <mm:nodeinfo type="type" id="my_type" write="false" />
      <mm:compare referid="my_type" value="<%= otherManagerName %>">
        <mm:field name="number" id="relatednode" write="false" />
        <tr>
          <td class="left"><mm:maywrite><a href="<mm:url page="edit_object.jsp">
            <mm:param name="nr"><mm:field name="number" /></mm:param>
          </mm:url>" title="edit node"><img src="img/mmbase-edit.png" alt="edit node" width="21" height="20" /></a></mm:maywrite>
          </td>
          <td class="relgui"> <mm:function name="gui" /> </td>
          <td class="buttons">
            <a onclick="toggle('edit_<mm:write referid="relation" />');return false;" href="#" title="edit or delete relation"><img src="img/mmbase-<%= arrow %>.png" alt="edit relation" width="21" height="20" /></a>
          </td>
        </tr>
        <tr style="display: none;" id="edit_<mm:write referid="relation" />">
          <td colspan="3" class="editrelation">
            <%-- edit relation --%>
            <mm:compare referid="relation" value="$rel"><mm:present referid="changerel">
              <strong class="message">The relation is changed.</strong>
            </mm:present></mm:compare>
            <mm:node number="$relation">
              <mm:form>
              <fieldset>
              <input name="rel" type="hidden" value="<mm:write referid="relation" />" />
              <input name="nr" type="hidden" value="<mm:write referid="nr" />" />
              <mm:maywrite><mm:import reset="true" id="formtype">input</mm:import></mm:maywrite>
              <mm:maywrite inverse="true"><mm:import id="formtype">guivalue</mm:import></mm:maywrite>
              <mm:fieldlist type="edit" fields="owner" id="fields${relation}">
                <div class="row">
                  <label for="mm_<mm:fieldinfo type="name" />">
                    <strong><mm:fieldinfo type="guiname" /></strong>
                    <mm:fieldinfo type="description"><mm:isnotempty><a onmouseover="showBox('descr_<mm:fieldinfo type="name" />',event);return false;" onmouseout="showBox('descr_<mm:fieldinfo type="name" />',event);return false;"><mm:fieldinfo type="name" /></a></mm:isnotempty></mm:fieldinfo>
                    <mm:fieldinfo type="description"><mm:isempty><mm:fieldinfo type="name" /></mm:isempty></mm:fieldinfo>
                  </label>
                  <mm:fieldinfo type="$formtype" />
                  <mm:fieldinfo type="description"><mm:isnotempty><div class="description" id="descr_<mm:fieldinfo type="name" />"><mm:write /></div></mm:isnotempty></mm:fieldinfo>
                </div>
              </mm:fieldlist>
              <div class="row">
                <label class="rel">&nbsp;</label>
                <mm:maywrite><input type="submit" name="changerel" value="Change" /></mm:maywrite>
                <mm:maydelete><input type="submit" name="deleterel" value="Delete" /></mm:maydelete>
              </div>
              </fieldset>
              </mm:form>
            </mm:node>
            
          </td>
        </tr>
      </mm:compare>
      </mm:relatednode>
      </mm:context>
    </mm:listrelations>
    <tr>
      <td colspan="3" class="pager"> <!-- max: ${max} size: ${size} offset: ${offset} -->
        <mm:isgreaterthan referid="size" value="0">
          <mm:previousbatches max="1">
            <a class="pageleft" href="<mm:url referids="nr,_@offset,max" />">&laquo;&laquo;</a>
          </mm:previousbatches>
          <mm:nextbatches max="1">
            <a class="pageright" href="<mm:url referids="nr,_@offset,max" />">&raquo;&raquo;</a>
          </mm:nextbatches>
        </mm:isgreaterthan>
      </td>
    </tr>       
    </mm:listrelationscontainer>
<%
}   // end while for parents
if (c == 0) {
%>
  <tr><td class="left">&nbsp;</td><td class="last" colspan="2">No relations in this direction available.</td></tr>
<%
}
%>
</tbody>
</table>
