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
            <mm:listnodes type="typerel" constraints="snumber=$typedefNumber" jspvar="typerelNode">
            <%
                // what is our relation type?
                Node relationDefinition = typerelNode.getNodeValue("rnumber");
                // what is the nodemanager, on the otherside?
                NodeManager otherNodeType = cloud.getNodeManager(typerelNode.getNodeValue("dnumber").getStringValue("name"));
            %>
            <tr>
                <td class="data">
                    <%=otherNodeType.getGUIName()%> (<%=relationDefinition.getValue("gui(dname)")%>)
                </td>
                <th colspan="3"><%=m.getString("relations.relations")%></th>
                <th colspan="3"><%=m.getString("relations.related")%></th>
                <td class="navigate">
                    <%-- <%= m.getString("new_relation.new")%> --%>
                    <a href='<mm:url page="new_relation.jsp" >
                        <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
                        <mm:param name="node_type"><%= otherNodeType.getName()%></mm:param>
                        <mm:param name="role_name"><%= relationDefinition.getStringValue("sname") %></mm:param>
                        <mm:param name="direction">create_child</mm:param><%-- WHY CANT I USE DNAME as ROLE????  --%>
                        </mm:url>'>
                       <span class="create"></span><span class="alt">+</span>
                   </a>
                </td>
            </tr>
            <%-- list all nodesof this specific relation type.. --%>
            <mm:import id="insrelWhereClause">(snumber=<mm:field referid="this_node_number" />) and (rnumber=<%=relationDefinition.getNumber()%>)</mm:import>
            <mm:listnodes type="insrel" constraints="$insrelWhereClause" jspvar="insrelNode">
            <%-- only display if the typerel nodemanager type, matches the type we have here.. --%>
            <% if(insrelNode.getNodeValue("dnumber").getNodeManager().getName().equals(otherNodeType.getName())) { %>
            <tr>
                <%-- skip first field --%>
                <td>&nbsp;</td>
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <%-- code below needed, since everything returned by insrel is a insrel node,.. not the actual builder --%>
                    <%= cloud.getNode(insrelNode.getNumber()).getStringValue("sgui($config.session,)") %>
                </td>
                <td class="navigate">
                    <%-- delete the relation node, not sure about the node_type argument! --%>
                    <a href='<mm:url page="commit_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
            <mm:param name="node_type"><%= insrelNode.getNodeManager().getName() %></mm:param>
                        <mm:param name="delete">true</mm:param>
		    </mm:url>' >
                      <span class="delete"></span><span class="alt">x</span>
                    </a>

                    <%-- edit the relation --%>
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
		    </mm:url>' >
                      <span class="select"></span><span class="alt">-&gt;</span>
                    </a>
                </td>
                <%  String destinationNodeNumber = insrelNode.getStringValue("dnumber"); %>
                <mm:node number='<%=destinationNodeNumber %>' id="node_number">
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="sgui($config.session,)" />
                </td>
                <td class="navigate">
                    <%-- edit the related node --%>
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok,node_number"/>'>
                      <span class="select"></span><span class="alt">-&gt;</span>
                    </a>
                </td>
                <%-- skip last field --%>
                <td>&nbsp;</td>
                </mm:node>
                <mm:remove referid="node_number" />
            </tr>
            <% } %>
            </mm:listnodes>
            <mm:remove referid="insrelWhereClause" />
            <%-- END: list all nodesof this specific relation type.. --%>
            </mm:listnodes>
            <%-- END: list all relation types, where we are the source --%>
        </mm:context>

        <tr>
            <th colspan="8"><%=m.getString("relations.to")%></th>
        </tr>
        <mm:context>
            <%-- list all relation types, where we are the source --%>
            <mm:listnodes type="typerel" constraints="dnumber=$typedefNumber" jspvar="typerelNode">
            <%
                // what is our relation type?
                Node relationDefinition = typerelNode.getNodeValue("rnumber");
                // what is the nodemanager, on the otherside?
                NodeManager otherNodeType = cloud.getNodeManager(typerelNode.getNodeValue("snumber").getStringValue("name"));
            %>
            <tr>
                <td class="data">
                    <%-- begin gomez friendly code --%>
                    <% if(relationDefinition.getIntValue("dir")==1) { %><small><% } %>
                    <%-- end gomez friendly code --%>
                    <%=otherNodeType.getGUIName()%> (<%=relationDefinition.getValue("gui(sname)")%>)
                    <% if(relationDefinition.getIntValue("dir")==1) { %><%=m.getString("relations.hidden_relation")%></small><% } %>
                </td>
                <th colspan="3"><%=m.getString("relations.relations")%></th>
                <th colspan="3"><%=m.getString("relations.related")%></th>
                <td class="navigate">
                    <%-- <%= m.getString("new_relation.new")%> --%>
                    <a href='<mm:url page="new_relation.jsp" >
                        <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
                        <mm:param name="node_type"><%= otherNodeType.getName()%></mm:param>
                        <%-- WHY CANT I USE DNAME as ROLE???? --%>
                        <mm:param name="role_name"><%= relationDefinition.getStringValue("sname") %></mm:param>
                        <mm:param name="direction">create_parent</mm:param>
                        <%-- END WHY CANT I USE DNAME as ROLE???? --%>
                        </mm:url>'>
                      <span class="create"></span><span class="alt">+</span>
                   </a>
                </td>
            </tr>
            <%-- list all nodesof this specific relation type.. --%>
            <mm:import id="insrelWhereClause">(dnumber=<mm:field referid="this_node_number" />) and (rnumber=<%=relationDefinition.getNumber()%>)</mm:import>
            <%-- insrel query <mm:write referid="insrelWhereClause" /> --%>
            <mm:listnodes type="insrel" constraints="${insrelWhereClause}" jspvar="insrelNode">
            <%-- only display if the typerel nodemanager type, matches the type we have here.. --%>
            <% if(insrelNode.getNodeValue("snumber").getNodeManager().getName().equals(otherNodeType.getName())) { %>
            <tr>
                <%-- skip first field --%>
                <td>&nbsp;</td>
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <%-- code below needed, since everything returned by insrel is a insrel node,.. not the actual builder --%>
                    <%= cloud.getNode(insrelNode.getNumber()).getStringValue("sgui($config.session,)") %>
                </td>
                <td class="navigate">
                    <%-- delete the relation node, not sure about the node_type argument! --%>
                    <a href='<mm:url page="commit_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
            <mm:param name="node_type"><%= insrelNode.getNodeManager().getName() %></mm:param>
                        <mm:param name="delete">true</mm:param>
		    </mm:url>' >
                      <span class="delete"></span><span class="alt">x</span>
                    </a>

                    <%-- edit the relation --%>
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
		    </mm:url>' >
                      <span class="select"></span><span class="alt">-&gt;</span>
                    </a>
                </td>
                <%  String sourceNodeNumber = insrelNode.getStringValue("snumber"); %>
                <mm:node number='<%=sourceNodeNumber %>' id="node_number">
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="sgui($config.session,)" />
                </td>
                <td class="navigate">
                    <%-- edit the related node --%>
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok,node_number"/>'>
                      <span class="select"></span><span class="alt">-&gt;</span>
                    </a>
                </td>
                <%-- skip last field --%>
                <td>&nbsp;</td>
                </mm:node>
                <mm:remove referid="node_number" />
            </tr>
            <% } %>
            </mm:listnodes>
            <mm:remove referid="insrelWhereClause" />
            <%-- END: list all nodesof this specific relation type.. --%>
            </mm:listnodes>
            <%-- END: list all relation types, where we are the source --%>
        </mm:context>

    </table>
  </mm:node>
</mm:context>
