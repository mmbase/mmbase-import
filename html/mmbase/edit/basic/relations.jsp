<%-- Listing relations is rather dirty.
     We wait for new bridge features. --%>
<mm:context id="relations">
  <%-- make sure the following variables are set --%>
  <mm:import externid="backpage_cancel" required="true" from="parent"/>
  <mm:import externid="backpage_ok"     required="true" from="parent"/>


  <%-- Make sure that we are in a node;
       not specifying by 'numer' or 'referid' attribute makes the tag look for a parent 'NodeProvider' --%>
  <mm:node id="this_node" jspvar="node">

    <mm:field id="this_node_number" name="number" write="false" />


    <%-- Determin the number of the nodemanager --%>
    <mm:field  name="otype" id="typedefNumber" write="false" />

    <table class="list" summary="relation overview" width="100%">
        <tr>
            <th colspan="8"><%=m.getString("relations.from")%></th>
        </tr>
        <mm:context>

            <%-- list all relation types, where we are the source --%>
            <% RelationManagerIterator relIterator = node.getNodeManager().getAllowedRelations((NodeManager) null, null, "source").relationManagerIterator(); 
               while(relIterator.hasNext()) {
                    RelationManager relationManager = relIterator.nextRelationManager();
                    // what is the nodemanager, on the otherside?
                    NodeManager otherNodeType =  relationManager.getSourceManager();
                    int rnumber = relationManager.getIntValue("rnumber");
                    String role = relationManager.getForwardRole();
            %>
            <%@ include file="relation.jsp" %>

        <mm:context>
        </mm:context>

        <tr>
            <th colspan="8"><%=m.getString("relations.to")%></th>
        </tr>

            <%-- list all relation types, where we are the source --%>
            <% relIterator = node.getNodeManager().getAllowedRelations((NodeManager) null, null, "destination").relationManagerIterator(); 
               while(relIterator.hasNext()) {
                    RelationManager relationManager = relIterator.nextRelationManager();
                    // what is the nodemanager, on the otherside?
                    NodeManager otherNodeType =  relationManager.getDestinationManager();
                    int rnumber = relationManager.getIntValue("rnumber");
                    String role = relationManager.getReciprocalRole();
            %>
             <%@ include file="relation.jsp" %>

        </mm:context>

    </table>
  </mm:node>
</mm:context>
