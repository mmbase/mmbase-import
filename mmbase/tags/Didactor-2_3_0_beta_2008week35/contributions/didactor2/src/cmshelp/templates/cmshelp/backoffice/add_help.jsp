<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud>
<mm:import externid="listjsp" required="true"/>
<mm:import externid="wizardjsp" required="true"/>
<mm:import id="help_nodes_exist" reset="true">false</mm:import>
<mm:listnodes type="helpnodes">
  <mm:import id="help_nodes_exist" reset="true">true</mm:import>
</mm:listnodes>

<mm:listnodes type="helpcontainers">    
  <mm:first>  
    <mm:node>
        <mm:field id="helpcontainernode" name="number" write="false" />
    </mm:node> 
  </mm:first>   
</mm:listnodes>   
  
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
     <td><a href='javascript:clickNode("help_root")'><img src="gfx/tree_plus.gif" border="0" align="middle" id='img_help_root'/></a></td>
     <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_help_root'/></td>			               
     <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/help/roothelpnodes&origin=<mm:write referid="helpcontainernode"/>&nodepath=helpnodes&fields=name&metadata=yes&path=Help' title="Help" target="text">Help</a></nobr></td> 
  </tr>
</table>
<div id="help_root" style="display:none"> 
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
			      <mm:compare referid="help_nodes_exist" value="true">
			         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <mm:compare referid="help_nodes_exist" value="true" inverse="true">
			         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
			      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/roothelpnodes&origin=<mm:write referid="helpcontainernode"/>&objectnumber=new&path=Help' title="nieuwe help" target="text">nieuwe help</a></nobr></td>
			   </tr>
			</table>    
	   <mm:listnodes type="helpcontainers">
           <mm:relatednodescontainer type="helpnodes" >
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
														<mm:listcontainer path="helpnodes,related,simplecontents">
														<mm:constraint operator="equal" field="helpnodes.number" referid="this"/>													    		
																<mm:list>
																  <mm:first>
														         <%hasChilds = true; %>   
																  </mm:first>																														       
																</mm:list>
														</mm:listcontainer>																			   									
												</mm:node> 									
												<td><a href='javascript:clickNodePortal("help_node_<%=nb%>")'><img src="gfx/adds/tree_plus.gif" border="0" align="middle" id='img_help_node_<%=nb%>'/></a></td>
												<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_help_node_<%=nb%>'/></td>
												<td><a href='<mm:write referid="wizardjsp"/>&wizard=config/help/helpnodes&objectnumber=<mm:field name="number"/>&path=Help > <mm:field name="name"/>' title="edit" target="text"><mm:field name="name"/></a></td>
										</tr>	
										</table>	
										<div id='help_node_<%=nb%>' style="display:none">							
												<mm:node number="$this">
												<mm:import id="helpname" jspvar="helpname" reset="true"><mm:field name="name"/></mm:import>
														<mm:listcontainer path="helpnodes,related,simplecontents">
														<mm:constraint operator="equal" field="helpnodes.number" referid="this"/>													    		
																<mm:list>
																  <mm:first>
														         <%hasSimpleContents = true; %>   
																  </mm:first>																														       
																</mm:list>
														</mm:listcontainer>			
														<mm:relatednodes role="childhnn" type="helpnodes">	
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
														      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/leafmaphelpnodes-origin&origin=<mm:write referid="this"/>&objectnumber=new&path=Help > <mm:field name="name"/>' title="nieuwe help map" target="text">nieuwe help map</a></nobr></td>
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
														      <%if(hasSimpleContents){%>
														         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
														      <%}else{%>														
														         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
														      <%}%>														      
														      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
														      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/newsimplecontents&origin=<mm:write referid="this"/>&objectnumber=new&path=Help > <mm:field name="name"/>' title="nieuwe help content" target="text">nieuwe help content</a></nobr></td>
														   </tr>
														</table> 																																																																													
												    <%levelTwoLast = false; %>
				                    <%@include file="related_helpnodes.jsp"%>							   									
												</mm:node> 	
										</div>						 	            			                  	                		                       			                    
                </mm:relatednodes>
	         </mm:relatednodescontainer>
	   </mm:listnodes>            
</div>
</mm:cloud>
