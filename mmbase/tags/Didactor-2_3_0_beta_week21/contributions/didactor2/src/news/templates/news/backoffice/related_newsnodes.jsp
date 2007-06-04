<mm:listcontainer path="newsnodes,related,simplecontents">
<mm:constraint operator="equal" field="newsnodes.number" referid="this"/>													    		
		<mm:list>
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					 <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
					 <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
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
					 <td><img src="gfx/learnblock.gif" border="0" align="middle" id='img2_news_node"/>'/></td>
					 <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/portalpages/simplecontents&objectnumber=<mm:field name="simplecontents.number"/>&path=<di:translate key="news.wordnews"/> > <%=newsname%>' title="edit" target="text"><mm:field name="simplecontents.title"/></a></nobr></td>
				</tr>
				</table> 
		</mm:list>
</mm:listcontainer>	
