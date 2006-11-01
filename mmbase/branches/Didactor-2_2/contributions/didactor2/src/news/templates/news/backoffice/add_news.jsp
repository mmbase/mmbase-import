<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud>
<mm:import externid="listjsp" required="true"/>
<mm:import externid="wizardjsp" required="true"/>
<mm:import id="nodes_exist" reset="true">false</mm:import>
<mm:listnodes type="newsnodes">
  <mm:import id="nodes_exist" reset="true">true</mm:import>
</mm:listnodes>

<mm:listnodes type="newscontainers">    
  <mm:first>  
    <mm:node>
        <mm:field id="newscontainernode" name="number" write="false" />
    </mm:node> 
  </mm:first>   
</mm:listnodes>   
  
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
     <td><a href='javascript:clickNode("news_root")'><img src="gfx/tree_pluslast.gif" border="0" align="middle" id='img_news_root'/></a></td>
     <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_news_root'/></td>			               
     <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/cmsnews/rootnewsnodes&origin=<mm:write referid="newscontainernode"/>&nodepath=newsnodes&fields=name&metadata=yes&path=<di:translate key="news.wordnews"/>' title="Nieuws" target="text"><di:translate key="news.wordnews"/></a></nobr></td> 
  </tr>
</table>
<div id="news_root" style="display:none"> 
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
			      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
			      <mm:compare referid="nodes_exist" value="true">
			         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <mm:compare referid="nodes_exist" value="true" inverse="true">
			         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
			      </mm:compare>
			      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
			      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/cmsnews/rootnewsnodes&origin=<mm:write referid="newscontainernode"/>&objectnumber=new&path=<di:translate key="news.wordnews"/>' title="nieuwe nieuws categorie" target="text">nieuwe nieuws categorie</a></nobr></td>
			   </tr>
			</table>    
	   <mm:listnodes type="newscontainers">
           <mm:relatednodescontainer type="newsnodes" >
                <mm:sortorder field="number" direction="up" />
	              <mm:relatednodes>
	                  <% hasChilds = false; %>  
	                  <% hasSimpleContents = false; %>  
	                  <% nb++; %> 
	                  <% childHasChilds = false; %>
			              <table border="0" cellpadding="0" cellspacing="0">	
			              <tr>	                    
												<td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
												<td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
												<mm:last inverse="true">
												  <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
												</mm:last>
												<mm:last>
												  <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
												  <% levelOneLast = true; %>
												</mm:last>  
												<mm:import id="this" ><mm:field name="number"/></mm:import>
												<mm:node number="$this">
														<mm:listcontainer path="newsnodes,related,simplecontents">
														<mm:constraint operator="equal" field="newsnodes.number" referid="this"/>													    		
																<mm:list>
																  <mm:first>
														         <%hasChilds = true; %>   
																  </mm:first>																														       
																</mm:list>
														</mm:listcontainer>																			   									
												</mm:node> 									
												<td><a href='javascript:clickNodePortal("news_node_<%=nb%>")'><img src="gfx/adds/tree_plus.gif" border="0" align="middle" id='img_news_node_<%=nb%>'/></a></td>
												<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_news_node_<%=nb%>'/></td>
												<td><a href='<mm:write referid="wizardjsp"/>&wizard=config/cmsnews/newsnodes&objectnumber=<mm:field name="number"/>&path=<di:translate key="news.wordnews"/> > <mm:field name="name"/>' title="edit" target="text"><mm:field name="name"/></a></td>																								
										</tr>	
										</table>	
										<div id='news_node_<%=nb%>' style="display:none">							
												<mm:node number="$this">
												<mm:import id="newsname" jspvar="newsname" reset="true"><mm:field name="name"/></mm:import>
														<mm:listcontainer path="newsnodes,related,simplecontents">
														<mm:constraint operator="equal" field="newsnodes.number" referid="this"/>													    		
																<mm:list>
																  <mm:first>
														         <%hasSimpleContents = true; %>   
																  </mm:first>																														       
																</mm:list>
														</mm:listcontainer>			
														<table border="0" cellpadding="0" cellspacing="0">
														   <tr>
														      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
														      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
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
														      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/portalpages/newsimplecontents&origin=<mm:write referid="this"/>&objectnumber=new&path=<di:translate key="news.wordnews"/> > <mm:field name="name"/>' title="nieuwe nieuws item" target="text">nieuwe nieuws item</a></nobr></td>
														   </tr>
														</table> 																																																																													
												    <%levelTwoLast = false; %>
												    <%@include file="related_newsnodes.jsp"%>												    
												</mm:node> 	
										</div>						 	            			                  	                		                       			                    
                </mm:relatednodes>
	         </mm:relatednodescontainer>
	   </mm:listnodes>            
</div>
</mm:cloud>
