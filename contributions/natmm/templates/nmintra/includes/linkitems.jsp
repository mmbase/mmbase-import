<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,shorty"
	orderby="contentrel.pos" directions="UP"
	><mm:field name="contentrel.pos" jspvar="posrel_pos" vartype="String" write="false"
	><% if(((Integer.parseInt(posrel_pos)-1) % numberOfColumns) == (colNumber-1)){ 
		%><mm:field name="shorty.number" jspvar="items_number" vartype="String" write="false"
		><mm:node number="<%= items_number %>" 
			><% String itemsClassName = cssClassName; 
			%><mm:relatednodes type="style"
			><mm:field name="title" jspvar="style_title" vartype="String" write="false"
				><% itemsClassName = style_title; 
			%></mm:field
			></mm:relatednodes
		
			><table cellpadding="0" cellspacing="0"  border="0">
			<tr><td><span class="dark_<%= itemsClassName %>"><span class="pageheader"><mm:field name="name" /></span></span></td></tr>
			<tr><td><img src="media/spacer.gif" width="10" height="1"></td></tr>
			<mm:related path="posrel,link"
				orderby="posrel.pos" directions="UP"
				><tr><td><a target="_blank" href="<mm:field name="link.url" 
						/>"><span class="normal"><mm:field name="link.titel" /></span></a>
				</td></tr>
			</mm:related
			></table>
			<img src="media/spacer.gif" width="100" height="10">
		</mm:node
		></mm:field><% 
	} 
	%></mm:field
></mm:list>
	
