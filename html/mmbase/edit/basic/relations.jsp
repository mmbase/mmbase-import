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

<mm:import id="tn" vartype="Integer" jspvar="thisnumber"><mm:write referid="thisnumber" /></mm:import>

<mm:import id="thiswhere">snumber=<mm:write referid="thisnumber" /> or dnumber=<mm:write referid="thisnumber" /></mm:import>
<table class="edit" summary="relation overview" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr><th colspan="3">Relations to this node</th></tr>
<mm:listnodes type="typerel" constraints="${thiswhere}" jspvar="l_node">
 <mm:context>
   <tr><!-- row for every type relations -->
   <%
    Node other = null; // the other nodemanager as a node.
    Node rel = null;
    NodeManager otherman = null;
    // find all possible relations
    int rnumber = l_node.getIntValue("rnumber");
    int snumber = l_node.getIntValue("snumber");
    int dnumber = l_node.getIntValue("dnumber");
	rel = cloud.getNode(rnumber);
	if (dnumber == thisnumber.intValue()) {
	    other = cloud.getNode(snumber);
	} else {
	    other = cloud.getNode(dnumber);
	}
	otherman =  cloud.getNodeManager(other.getStringValue("name"));     %>	
   <!-- first column: present relation type -->
   <td class="data"><%=rel.getValue("gui(dname)")%> <%=otherman.getGUIName()%>:</td>
	<mm:import id="thiswhere2">(snumber=<mm:field referid="thisnodenumber" /> or (dnumber=<mm:field referid="thisnodenumber"/> and dir<>1)) and (rnumber=<%=rnumber%>)</mm:import>

  <td class="data">
  <!-- second column, overview of existing relations of this type -->
  &nbsp;<table class="edit" cellspacing="1" cellpadding="3" border="0"  summary="relations of this type" width="100%">
  <mm:listnodes type="insrel" constraints="${thiswhere2}" jspvar="rel_node"><%
    Node reln = (node.getNumber() == rel_node.getIntValue("snumber")) ?
                   cloud.getNode(rel_node.getIntValue("dnumber")) :
                   cloud.getNode(rel_node.getIntValue("snumber"));
    if (reln.getNodeManager().getName().equals(otherman.getName())) {
       %><tr>
	   <td class="data" width="60%">
	   <%try{%><%=cloud.getNode(reln.getNumber()).getStringValue("gui()")%><%}catch(Exception e){}%>&nbsp;</td>
	    <!-- go to the related node -->
       <td class="data">related node</td>
       <td class="navigate">
       <a href='<mm:url page="change_node.jsp" >
                <mm:param name="node_number"><%=reln.getNumber()%></mm:param>
		</mm:url>' ><img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" /></a>
	    </td>
		<!-- go to relation itself -->
        <td class="data">relation</td>	   
		<!-- delete the relation node -->
		<td class="navigate">
        	<a href='<mm:url page="commit_node.jsp" referids="backpage_cancel,backpage_ok">
            	<mm:param name="node_number"><%=rel_node.getNumber()%></mm:param>
				<mm:param name="node_type"><%= reln.getNodeManager().getName() %></mm:param>
            	<mm:param name="delete">true</mm:param>				
				</mm:url>' ><img src="images/delete.gif" alt="->" border="0" width="20" height="20" align="right" /></a>
        </td>
		<td class="navigate">
		<!-- go to the relation node -->
        	<a href='<mm:url page="change_node.jsp" referids="backpage_cancel,backpage_ok">
            	<mm:param name="node_number"><%=rel_node.getNumber()%></mm:param>
				</mm:url>' ><img src="images/select.gif" alt="->" border="0" width="20" height="20" align="right" /></a>
		</td>
		</tr> <%
    }
   %>
  </mm:listnodes>
  </table>
  </td><!-- end of second column -->

  <td>&nbsp;</td> <!-- empty column -->
  </tr>
  <tr><!-- a row with three columns -->
  <td class="data">&nbsp;</td> <!-- empty -->
  <td class="data">new relation</td>
  <td class="navigate"> <!-- + -->
  <a href='<mm:url page="new_relation.jsp" >
           <mm:param name="node"><mm:field node="this_node" name="number" /></mm:param>
           <mm:param name="node_type"><%=otherman.getName()%></mm:param>
           <mm:param name="role_name"><%=rel.getStringValue("sname")%></mm:param>
           </mm:url>'><img src="images/create.gif" alt="->" width="20" height="20" border="0" align="right" />
   </a>
   </td>
   </tr>
   <tr><td class="search" colspan="3"><hr /></td></tr>
</mm:context>
</mm:listnodes>
</table>
</mm:context>
