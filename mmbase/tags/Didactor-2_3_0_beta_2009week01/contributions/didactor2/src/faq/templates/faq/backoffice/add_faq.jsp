<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud>
<mm:import externid="listjsp" required="true"/>
<mm:import externid="wizardjsp" required="true"/>
<mm:import id="nodes_exist" reset="true">false</mm:import>

<!--
     This jsp is a horrible mess
-->

<mm:listnodes type="faqnodes">
  <mm:import id="nodes_exist" reset="true">true</mm:import>
</mm:listnodes>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
     <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
     <td><a href='javascript:clickNode("faq_root")'><img src="gfx/tree_plus.gif" border="0" align="middle" id='img_faq_root'/></a></td>
     <td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_faq_root'/></td>
     <td><nobr>&nbsp;<a href='<mm:write referid="listjsp"/>&wizard=config/faq/rootfaqnodes&nodepath=faqnodes&fields=name&metadata=yes&path=FAQ' title="FAQ" target="text">FAQ</a></nobr></td>
  </tr>
</table>
<div id="faq_root" style="display:none">
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
			      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/faq/rootfaqnodes&objectnumber=new&path=FAQ' title="nieuwe FAQ" target="text">nieuwe FAQ</a></nobr></td>
			   </tr>
			</table>
      <mm:listnodescontainer type="faqnodes" >
                <mm:sortorder field="number" direction="up" />
	              <mm:listnodes>
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
														<mm:listcontainer path="faqnodes,related,faqitems">
														<mm:constraint operator="equal" field="faqnodes.number" referid="this"/>
																<mm:list>
																  <mm:first>
														         <%hasChilds = true; %>
																  </mm:first>
																</mm:list>
														</mm:listcontainer>
												</mm:node>
												<td></td>
												<td><img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_faq_node_<%=nb%>'/></td>
												<td><a href='<mm:write referid="wizardjsp"/>&wizard=config/faq/faqnodes&objectnumber=<mm:field name="number"/>&path=FAQ > <mm:field name="name"/>' title="edit" target="text"><mm:field name="name"/></a></td>
										</tr>
										</table>
										<div id='faq_node_<%=nb%>' style="display:none">
												<mm:node number="$this">
												<mm:import id="faqname" jspvar="faqname" reset="true"><mm:field name="name"/></mm:import>
														<mm:listcontainer path="faqnodes,related,faqitems">
														<mm:constraint operator="equal" field="faqnodes.number" referid="this"/>
																<mm:list>
																  <mm:first>
														         <%hasSimpleContents = true; %>
																  </mm:first>
																</mm:list>
														</mm:listcontainer>
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
														      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/faq/newfaqitems&origin=<mm:write referid="this"/>&objectnumber=new&path=FAQ > <mm:field name="name"/>' title="nieuwe FAQ item" target="text">nieuwe FAQ item</a></nobr></td>
														   </tr>
														</table>
												    <%levelTwoLast = false; %>
												    <%@include file="related_faqnodes.jsp"%>
												</mm:node>
										</div>
                </mm:listnodes>
	         </mm:listnodescontainer>
</div>
</mm:cloud>
