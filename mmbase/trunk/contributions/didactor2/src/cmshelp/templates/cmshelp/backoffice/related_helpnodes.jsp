<mm:relatednodes role="childhnn" type="helpnodes">	
<mm:constraint operator="equal" field="number" referid="this"/>
    <% nbc++; %> 
    <%childHasSimpleContents = false; %>      
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>	                    
				<td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>				
				<td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
				<%if(!levelOneLast){%>
				   <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
				<%}else{%>
				   <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
				<%}%>
				<mm:last inverse="true">
				  <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
				</mm:last>
				<mm:last>
				   <%if(!hasSimpleContents){%>			
				      <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
				   <%}else{%> 
				      <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
				   <%}%>  
				   <% levelTwoLast = true; %>
				</mm:last> 
				<mm:import id="childnb" ><mm:field name="number"/></mm:import>
				<mm:node id="find"  number="$childnb">
				
						<mm:listcontainer path="helpnodes,related,simplecontents">
						<mm:constraint operator="equal" field="helpnodes.number" referid="childnb"/>													    		
								<mm:list>
								  <mm:first>
						         <%childHasSimpleContents = true; %>   
								  </mm:first>																														       
								</mm:list>
						</mm:listcontainer>																			   									
				</mm:node> 				
						<td><a href='javascript:clickNodePortal("help_child_<%=nbc%>")'><img src="gfx/adds/tree_plus.gif" border="0" align="middle" id='img_help_child_<%=nbc%>'/></a></td>
						<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_help_child_<%=nbc%>'/></td>
						<td><a href='<mm:write referid="wizardjsp"/>&wizard=config/help/leafhelpnodes&objectnumber=<mm:field name="number"/>&path=Help > <%=helpname%>' title="edit" target="text"><mm:field name="name"/></a></td>				 	
		</tr>
		</table> 
		<div id='help_child_<%=nbc%>' style="display:none">		 		
				<mm:import id="thischild"><mm:field name="number"/></mm:import>
				<mm:node number="$thischild">
				<mm:import id="childnameforhelp" jspvar="childnameforhelp" reset="true"><mm:field name="name"/></mm:import>
						<table border="0" cellpadding="0" cellspacing="0">
						   <tr>
						      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
						      <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
						      <%if(!levelOneLast){%>
						         <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
						      <%}else{%>														
						         <td><img src="gfx/tree_spacer.gif" border="0" align="center" valign="middle"/></td>
						      <%}%>
						      <%if((!levelTwoLast)||(hasSimpleContents)){%>
						         <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
						      <%}else{%>														
						         <td><img src="gfx/tree_spacer.gif" border="0" align="center" valign="middle"/></td>
						      <%}%>						      
						      <%if(childHasSimpleContents){%>
						         <td><img src="gfx/adds/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
						      <%}else{%>														
						         <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
						      <%}%>														      
						      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
						      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/newsimplecontents&origin=<mm:write referid="thischild"/>&objectnumber=new&path=Help > <%=helpname%> > <%=childnameforhelp %>' title="nieuwe help content" target="text">nieuwe help content</a></nobr></td>
						   </tr>
						</table>				
						<mm:listcontainer path="helpnodes,related,simplecontents">
						<mm:constraint operator="equal" field="helpnodes.number" referid="thischild"/>																
								<mm:list>
										<table border="0" cellpadding="0" cellspacing="0">
										<tr>
											 <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
											 <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
											 <%if(!levelOneLast){%>
												   <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
											 <%}else{%>
												   <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
											 <%}%>
											 <%if((!levelTwoLast)||(hasSimpleContents)){%>
												   <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
											 <%}else{%>
												   <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
											 <%}%>									 
											 <mm:last inverse="true">
											    <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
											 </mm:last>
											 <mm:last>
											    <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
											 </mm:last>
											 <td></td>
											 <td><img src="gfx/learnblock.gif" border="0" align="middle" id='img2_help_node"/>'/></td>
											 <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/simplecontents&objectnumber=<mm:field name="simplecontents.number"/>&path=Help > <%=helpname%> > <%=childnameforhelp %>' title="edit" target="text"><mm:field name="simplecontents.title"/></a></nobr></td>					                     			                     
										</tr>
										</table> 
								</mm:list>	
		        </mm:listcontainer>																															   														
				</mm:node>
		</div>	 			              			                  	                		                       			                    
</mm:relatednodes>	
<mm:listcontainer path="helpnodes,related,simplecontents">
<mm:constraint operator="equal" field="helpnodes.number" referid="this"/>													    		
		<mm:list>
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					 <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
					 <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
					 <%if(!levelOneLast){%>
						   <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
					 <%}else{%>
						   <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
					 <%}%>
					 <mm:last inverse="true">
					    <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
					 </mm:last>
					 <mm:last>
					    <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
					 </mm:last>
					 <td></td>
					 <td><img src="gfx/learnblock.gif" border="0" align="middle" id='img2_help_node"/>'/></td>
					 <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/help/simplecontents&objectnumber=<mm:field name="simplecontents.number"/>&path=Help > <%=helpname%>' title="edit" target="text"><mm:field name="simplecontents.title"/></a></nobr></td>					                     			                     
				</tr>
				</table> 
		</mm:list>
</mm:listcontainer>	
