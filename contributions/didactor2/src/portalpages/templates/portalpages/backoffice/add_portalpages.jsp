<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:cloud>
<mm:import externid="listjsp"   from="request" required="true"/>
<mm:import externid="wizardjsp" from="request" required="true"/>
<script type="text/javascript">
   function clickNodePortal(node) {
      var level = node.split('_').length;
      //saveCookie('lastnodepagina' + level, node,1);
      clickNode(node);
   }
</script>
<mm:import id="nodes_exist" reset="true">false</mm:import>
<mm:listnodes type="portalpagesnodes">
  <mm:import id="nodes_exist" reset="true">true</mm:import>
</mm:listnodes>

<mm:listnodes type="portalpagescontainers">
  <mm:first>
    <!--- WTF WTF WTF. This stuff is HORRIBLE. Taking a random node. Perhaps there is only one of this type, or so? -->
    <mm:node>
        <mm:field id="containernode" name="number" write="false" />
    </mm:node>
  </mm:first>
</mm:listnodes>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
     <td><a href='javascript:clickNode("portal_root")'><img src="gfx/tree_plus.gif" border="0" align="middle" id='img_portal_root'/></a></td>
     <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_portal_root'/></td>
     <mm:link referid="listjsp" referids="containernode@origin">
       <mm:param name="wizard">config/portalpages/rootportalpagesnodes</mm:param>
       <mm:param name="nodepath">portalpagesnodes</mm:param>
       <mm:param name="fields">name</mm:param>
       <mm:param name="searchfields">name</mm:param>
       <mm:param name="metadata">yes</mm:param>
       <td><nobr><a href="${_}" title="portal pagina's" target="text">Portal Pagina's</a></nobr></td> <!-- I18N ? -->
     </mm:link>
  </tr>
</table>
<div id="portal_root" style="display:none">
<% boolean levelOneLast = false; %>
<% boolean levelTwoLast = false; %>
<% boolean hasChilds = false; %>
<% boolean childHasChilds = false; %>
<% boolean hasSimpleContents = false; %>
<% boolean childHasSimpleContents = false; %>
<% int nb = 0; %>
<% int nbc = 0; %>
			<table border="0" cellpadding="0" cellspacing="0">
			   <tr>
			      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
			      <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
			      <mm:compare referid="nodes_exist" value="true">
			         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <mm:compare referid="nodes_exist" value="true" inverse="true">
			         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <mm:link referid="wizardjsp" referids="containernode@origin">
              <mm:param name="wizard">config/portalpages/rootportalpagesnodes</mm:param>
              <mm:param name="objectnumber">new</mm:param>
              <td><a href="${_}" title="nieuwe map" target="text">nieuwe map</a></td>
            </mm:link>
			   </tr>
			</table>
	   <mm:listnodes type="portalpagescontainers">
           <mm:relatednodescontainer type="portalpagesnodes" >
                <mm:sortorder field="number" direction="up" />
	              <mm:relatednodes>
	                  <% hasChilds = false; %>
	                  <% hasSimpleContents = false; %>
	                  <% nb++; %>
	                  <% childHasChilds = false; %>
			              <table border="0" cellpadding="0" cellspacing="0">
			              <tr>
												<td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
												<td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
												<mm:last inverse="true">
												  <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
												</mm:last>
												<mm:last>
												  <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
												  <% levelOneLast = true; %>
												</mm:last>
												<mm:import id="this" ><mm:field name="number"/></mm:import>
												<mm:node number="$this">
														<mm:listcontainer path="portalpagesnodes,related,simplecontents">
														<mm:constraint operator="equal" field="portalpagesnodes.number" referid="this"/>
																<mm:list>
																  <mm:first>
														         <%hasChilds = true; %>
																  </mm:first>
																</mm:list>
														</mm:listcontainer>
												</mm:node>
												<mm:relatednodes role="childppnn" type="portalpagesnodes">
												<mm:constraint operator="equal" field="number" referid="this"/>
													  <mm:first>
											         <%hasChilds = true; %>
													  </mm:first>
									      </mm:relatednodes>
												<td><a href='javascript:clickNodePortal("portal_node_<%=nb%>")'><img src="gfx/adds/tree_plus.gif" border="0" align="middle" id='img_portal_node_<%=nb%>'/></a></td>
												<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_portal_node_<%=nb%>'/></td>
                        <mm:link referid="wizardjsp" referids="_node@objectnumber">
                          <mm:param name="wizard">config/portalpages/portalpagesnodes</mm:param>
                          <mm:param name="objectnumber">new</mm:param>
                          <td><a href="${_}" title="edit" target="text"><mm:field name="name"/></a></td>
                        </mm:link>
										</tr>
										</table>
										<div id='portal_node_<%=nb%>' style="display:none">
												<mm:node id="current" number="$this">
														<mm:listcontainer path="portalpagesnodes,related,simplecontents">
														<mm:constraint operator="equal" field="portalpagesnodes.number" referid="this"/>
																<mm:list>
																  <mm:first>
														         <%hasSimpleContents = true; %>
																  </mm:first>
																</mm:list>
														</mm:listcontainer>
														<mm:relatednodes role="childppnn" type="portalpagesnodes">
                            <mm:constraint operator="equal" field="number" referid="this"/>
															  <mm:first>
													         <%childHasChilds = true; %>
															  </mm:first>
                            </mm:relatednodes>
														<table border="0" cellpadding="0" cellspacing="0">
														   <tr>
														      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
														      <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
														      <%if(!levelOneLast){%>
														         <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
														      <%}else{%>
														         <td><img src="gfx/tree_spacer.gif" border="0" align="center" valign="middle"/></td>
														      <%}%>
														      <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
														      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
                                  <mm:link referid="wizardjsp" referids="this@origin">
                                    <mm:param name="wizard">config/portalpages/leafmapportalpages-origin</mm:param>
                                    <mm:param name="objectnumber">new</mm:param>
                                    <td><a href="${_}" title="nieuwe map" target="text">nieuwe map</a></td>
                                  </mm:link>
														   </tr>
														</table>
														<table border="0" cellpadding="0" cellspacing="0">
														   <tr>
														      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
														      <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
														      <%if(!levelOneLast){%>
														         <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
														      <%}else{%>
														         <td><img src="gfx/tree_spacer.gif" border="0" align="center" valign="middle"/></td>
														      <%}%>
														      <%if(hasSimpleContents || childHasChilds){%>
														         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
														      <%}else{%>
														         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
														      <%}%>
														      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
														      <td>
                                    <mm:link referid="wizardjsp" referids="this@origin">
                                      <mm:param name="wizard">config/portalpages/newsimplecontents</mm:param>
                                      <mm:param name="objectnumber">new</mm:param>
                                      <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
                                    </mm:link>
                                  </td>
														   </tr>
														</table>
												    <%levelTwoLast = false; %>
				                    <%@include file="related_portalpagesnodes.jsp"%>
												</mm:node>
										</div>
                </mm:relatednodes>
	         </mm:relatednodescontainer>
	   </mm:listnodes>
</div>
</mm:cloud>
