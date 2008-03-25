<mm:relatednodes role="childppnn" type="portalpagesnodes">
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
						<mm:listcontainer path="portalpagesnodes,related,simplecontents">
						<mm:constraint operator="equal" field="portalpagesnodes.number" referid="childnb"/>
								<mm:list>
								  <mm:first>
						         <%childHasSimpleContents = true; %>
								  </mm:first>
								</mm:list>
						</mm:listcontainer>
				</mm:node>
						<td><a href='javascript:clickNodePortal("portal_child_<%=nbc%>")'><img src="gfx/adds/tree_plus.gif" border="0" align="middle" id='img_portal_child_<%=nbc%>'/></a></td>
						<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_portal_child_<%=nbc%>'/></td>
            <mm:link referid="wizardjsp" referids="_node@objectnumber">
              <mm:param name="wizard">config/portalpages/leafportalpagesnodes</mm:param>
              <td><a href="${_}" title="edit" target="text"><mm:field name="name"/></a></td>
            </mm:link>
		</tr>
		</table>
		<div id='portal_child_<%=nbc%>' style="display:none">
				<mm:import id="thischild"><mm:field name="number"/></mm:import>
				<mm:node number="$thischild">
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
                  <mm:link referid="wizardjsp" referids="thischild@origin">
                    <mm:param name="wizard">config/portalpages/newsimplecontents</mm:param>
                    <mm:param name="objectnumber">new</mm:param>
                    <td>
                      <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
                    </td>
                  </mm:link>
						   </tr>
						</table>
						<mm:listcontainer path="portalpagesnodes,related,simplecontents">
						<mm:constraint operator="equal" field="portalpagesnodes.number" referid="thischild"/>
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
											 <td><img src="gfx/learnblock.gif" border="0" align="middle" id='img2_portal_node"/>'/></td>
                       <mm:link referid="wizardjsp" referids="_node.simplecontents@objectnumber">
                         <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
                         <td>
                           <a href="${_}" target="text"><mm:field name="simplecontents.title"/></a>
                         </td>
                       </mm:link>
										</tr>
										</table>
								</mm:list>
		        </mm:listcontainer>
				</mm:node>
		</div>
</mm:relatednodes>
<mm:listcontainer path="portalpagesnodes,related,simplecontents">
<mm:constraint operator="equal" field="portalpagesnodes.number" referid="this"/>
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
					 <td><img src="gfx/learnblock.gif" border="0" align="middle" id='img2_portal_node"/>'/></td>
           <mm:link referid="wizardjsp" referids="_node.simplecontents@objectnumber">
             <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
             <td>
               <a href="${_}" title="edit" target="text"><mm:field name="simplecontents.title"/></a>
             </td>
           </mm:link>
				</tr>
				</table>
		</mm:list>
</mm:listcontainer>
