<!-- Listing relations is rather dirty, perhaps 'mm:lisrelation' could be used -->
<mm:context id="relations">
    <mm:import externid="backpage_cancel" required="true" from="parent"/>
    <mm:import externid="backpage_ok"     required="true" from="parent"/>
    <mm:import externid="this_node"       required="true" from="parent" vartype="Node" jspvar="node" />

    <mm:field id="thisnodenumber" name="number"> </mm:field>

    <mm:import id="thisnumber">0</mm:import>
    <mm:import id="typewhere">name='<mm:nodeinfo type="nodemanager" />'</mm:import>

    <mm:listnodes type="typedef" constraints="${typewhere}">
        <mm:remove referid="thisnumber" />
        <mm:import id="thisnumber"><mm:field name="number" /></mm:import>
    </mm:listnodes>

    <mm:import id="sourceWhereClause">snumber=<mm:write referid="thisnumber" /></mm:import>
    <mm:import id="tn" vartype="Integer" jspvar="thisnumber"><mm:write referid="thisnumber" /></mm:import>
    <table class="edit" summary="relation overview" width="100%" cellspacing="1" cellpadding="3" border="0">
        
        <tr>
            <th colspan="8"><%=m.getString("relations.to")%></th>
        </tr>       
        <mm:context>
            <!-- list all relation types, where we are the source -->
            <mm:import externid="sourceWhereClause" required="true" from="parent"/>
            <!-- typerel query <mm:write referid="sourceWhereClause" /> -->
            <mm:listnodes type="typerel" constraints="${sourceWhereClause}" jspvar="typerelNode">
            <%
                // what is our relation type?
                Node relationDefinition = typerelNode.getNodeValue("rnumber");
                // what is the nodemanager, on the otherside?
                NodeManager otherNodeType = cloud.getNodeManager(typerelNode.getNodeValue("dnumber").getStringValue("name"));
            %>            
            <tr>            
                <td class="data">
                    <a href="<%=response.encodeURL("change_node.jsp?node_number=" + typerelNode.getNumber())%>">
                    <%=otherNodeType.getGUIName()%></a>
                    (<a href="<%=response.encodeURL("change_node.jsp?node_number=" + relationDefinition.getNumber())%>">
                     <%=relationDefinition.getValue("gui(dname)")%></a>)
                </td>
                <th colspan="3"><%=m.getString("relations.relations")%></th>
                <th colspan="3"><%=m.getString("relations.related")%></th>
                <td class="navigate">
                    <!-- <%= m.getString("new_relation.new")%> -->
                    <a href='<mm:url page="new_relation.jsp" >
                        <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
                        <mm:param name="node_type"><%= otherNodeType.getName()%></mm:param>
                        <mm:param name="role_name"><%= relationDefinition.getStringValue("sname") %></mm:param>
                        <!-- WHY CANT I USE DNAME as ROLE????  -->
                        <mm:param name="direction">create_parent</mm:param>
                        <!-- END WHY CANT I USE DNAME as ROLE????  -->
                        </mm:url>'>
                        <img src="images/create.gif" alt="+" width="20" height="20" border="0" align="right" />
                   </a>                
                </td>
            </tr>
            <!-- list all nodesof this specific relation type.. -->
            <mm:import id="insrelWhereClause">(snumber=<mm:field referid="thisnodenumber" />) and (rnumber=<%=relationDefinition.getNumber()%>)</mm:import>
            <!-- insrel query <mm:write referid="insrelWhereClause" /> -->
            <mm:listnodes type="insrel" constraints="$insrelWhereClause" jspvar="insrelNode">
            <!-- only display if the typerel nodemanager type, matches the type we have here.. -->
            <% if(insrelNode.getNodeValue("dnumber").getNodeManager().getName().equals(otherNodeType.getName())) { %>
            <tr>
                <!-- skip first field -->
                <td>&nbsp;</td>
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="gui()" />
                </td>
                <td class="navigate">
                    <!-- delete the relation node, not sure about the node_type argument! -->
                    <a href='<mm:url page="commit_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
			<mm:param name="node_type"><%= insrelNode.getNodeManager().getName() %></mm:param>
                        <mm:param name="delete">true</mm:param>
		    </mm:url>' >
                        <img src="images/delete.gif" alt="x" border="0" width="20" height="20" align="right" />
                    </a>

                    <!-- edit the relation -->
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
		    </mm:url>' >
                        <img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" />
                    </a>
                </td>
                <%  String destinationNodeNumber = insrelNode.getStringValue("dnumber"); %>
                <mm:node number='<%=destinationNodeNumber %>' id="node_number">
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="gui()" />
                </td>
                <td class="navigate">
                    <!-- edit the related node -->
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok,node_number"/>'>
                        <img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" />
                    </a>
                </td>
                <!-- skip last field -->
                <td>&nbsp;</td>
                </mm:node>
                <mm:remove referid="node_number" />                
            </tr>
            <% } %>            
            </mm:listnodes>
            <mm:remove referid="insrelWhereClause" />
            <!-- END: list all nodesof this specific relation type.. -->
            </mm:listnodes>           
            <!-- END: list all relation types, where we are the source -->
        </mm:context>
        
        <mm:import id="destinationWhereClause">dnumber=<mm:write referid="thisnumber" /></mm:import>        
        <tr>
            <th colspan="8"><%=m.getString("relations.from")%></th>
        </tr>
        <mm:context>
            <!-- list all relation types, where we are the source -->
            <mm:import externid="destinationWhereClause" required="true" from="parent"/>
            <!-- typerel query <mm:write referid="destinationWhereClause" /> -->
            <mm:listnodes type="typerel" constraints="${destinationWhereClause}" jspvar="typerelNode">
            <%
                // what is our relation type?
                Node relationDefinition = typerelNode.getNodeValue("rnumber");
                // what is the nodemanager, on the otherside?
                NodeManager otherNodeType = cloud.getNodeManager(typerelNode.getNodeValue("snumber").getStringValue("name"));
            %>            
            <tr>            
                <td class="data">
                    <!-- begin gomez friendly code -->
                    <% if(relationDefinition.getIntValue("dir")==1) { %>[<% } %>
                    <!-- end gomez friendly code -->
                    <a href="<%=response.encodeURL("change_node.jsp?node_number=" + typerelNode.getNumber())%>">
                    <%=otherNodeType.getGUIName()%></a>
                    (<a href="<%=response.encodeURL("change_node.jsp?node_number=" + relationDefinition.getNumber())%>">
                     <%=relationDefinition.getValue("gui(sname)")%></a>)
                    <% if(relationDefinition.getIntValue("dir")==1) { %>]<% } %>
                </td>
                <th colspan="3"><%=m.getString("relations.relations")%></th>
                <th colspan="3"><%=m.getString("relations.related")%></th>
                <td class="navigate">
                    <!-- <%= m.getString("new_relation.new")%> -->
                    <a href='<mm:url page="new_relation.jsp" >
                        <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
                        <mm:param name="node_type"><%= otherNodeType.getName()%></mm:param>
                        <!-- WHY CANT I USE DNAME as ROLE???? -->
                        <mm:param name="role_name"><%= relationDefinition.getStringValue("sname") %></mm:param>
                        <mm:param name="direction">create_child</mm:param>
                        <!-- END WHY CANT I USE DNAME as ROLE???? -->
                        </mm:url>'>
                        <img src="images/create.gif" alt="+" width="20" height="20" border="0" align="right" />
                   </a> 
                </td>
            </tr>
            <!-- list all nodesof this specific relation type.. -->                        
            <mm:import id="insrelWhereClause">(dnumber=<mm:field referid="thisnodenumber" />) and (rnumber=<%=relationDefinition.getNumber()%>)</mm:import>
            <!-- insrel query <mm:write referid="insrelWhereClause" /> -->
            <mm:listnodes type="insrel" constraints="${insrelWhereClause}" jspvar="insrelNode">
            <!-- only display if the typerel nodemanager type, matches the type we have here.. -->
            <% if(insrelNode.getNodeValue("snumber").getNodeManager().getName().equals(otherNodeType.getName())) { %>
            <tr>
                <!-- skip first field -->
                <td>&nbsp;</td>
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="gui()" />
                </td>
                <td class="navigate">
                    <!-- delete the relation node, not sure about the node_type argument! -->
                    <a href='<mm:url page="commit_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
			<mm:param name="node_type"><%= insrelNode.getNodeManager().getName() %></mm:param>
                        <mm:param name="delete">true</mm:param>
		    </mm:url>' >
                        <img src="images/delete.gif" alt="x" border="0" width="20" height="20" align="right" />
                    </a>

                    <!-- edit the relation -->
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok">
                        <mm:param name="node_number"><%=insrelNode.getNumber()%></mm:param>
		    </mm:url>' >
                        <img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" />
                    </a>
                </td>
                <%  String sourceNodeNumber = insrelNode.getStringValue("snumber"); %>
                <mm:node number='<%=sourceNodeNumber %>' id="node_number">
                <td class="data">
                    #<mm:field name="number" />
                </td>
                <td class="data">
                    <mm:field name="gui()" />
                </td>
                <td class="navigate">
                    <!-- edit the related node -->
                    <a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok,node_number"/>'>
                        <img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" />
                    </a>
                </td>
                <!-- skip last field -->
                <td>&nbsp;</td>
                </mm:node>
                <mm:remove referid="node_number" />
            </tr>
            <% } %>
            </mm:listnodes>
            <mm:remove referid="insrelWhereClause" />
            <!-- END: list all nodesof this specific relation type.. -->
            </mm:listnodes>
            <!-- END: list all relation types, where we are the source -->
        </mm:context>

    </table>
</mm:context>
