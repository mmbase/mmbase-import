            <tr>
                <td class="data">
                    <%=otherNodeType.getGUIName()%> (<%=role%>)
                </td>
                <th colspan="3"><%=m.getString("relations.relations")%></th>
                <th colspan="3"><%=m.getString("relations.related")%></th>
                <td class="navigate">
                    <%-- <%= m.getString("new_relation.new")%> --%>
                    <a href='<mm:url page="new_relation.jsp" >
                        <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
                        <mm:param name="node_type"><%= otherNodeType.getName()%></mm:param>
                        <mm:param name="role_name"><%= relationManager.getForwardRole() %></mm:param>
                        <mm:param name="direction">create_child</mm:param>
                        </mm:url>'>
                       <span class="create"></span><span class="alt">+</span>
                   </a>
                </td>
            </tr>
            <%-- list all nodesof this specific relation type.. --%>
            <mm:import id="insrelWhereClause">(snumber=<mm:field referid="this_node_number" />) and (rnumber=<%=rnumber%>)</mm:import>
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
                    <%= cloud.getNode(insrelNode.getNumber()).getFunctionValue("gui", null).toString()%>
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
                    <mm:nodeinfo type="gui" />
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
            <% } %>
            <%-- END: list all relation types, where we are the source --%>
