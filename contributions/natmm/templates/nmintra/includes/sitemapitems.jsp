<mm:list nodes="<%= websiteId %>" path="rubriek1,parent,rubriek2"
	orderby="parent.pos" directions="UP"
    ><mm:field name="parent.pos" jspvar="parent_pos" vartype="String" write="false"
	><% if(((Integer.parseInt(parent_pos)-1) % numberOfColumns) == (colNumber-1)){ 
		%><mm:field name="rubriek2.number" jspvar="rubriek_number" vartype="String" write="false"
		><% String itemsClassName = "" ; 
		%><mm:list nodes="<%= rubriek_number %>" path="rubriek,style" fields="style.title"
			><mm:field name="style.title" jspvar="style_title" vartype="String" write="false"
				><% itemsClassName = style_title; 
			%></mm:field
		></mm:list
		
		
		><mm:node number="<%= rubriek_number %>" 
		><mm:related path="posrel,pagina" orderby="posrel.pos" directions="UP"
			><mm:first
				><table cellpadding="0" cellspacing="0" border="0">
				<tr><td><a href=<%@include file="..\includes\pageurl.jsp" 
					%>><span class="dark_<%= itemsClassName %>"><span class="pageheader"><mm:field name="rubriek.naam" 
					/></span></span></a></td></tr>
				<tr><td><img src="media/spacer.gif" width="10" height="1"></td></tr></mm:first
			><mm:field name="posrel.pos" jspvar="posrel2_pos" vartype="String" write="false"
			><%	if(!posrel2_pos.equals("0")) { 
				%><tr><td><a href=<%@include file="..\includes\pageurl.jsp" 
					%>><span class="normal"><mm:field name="pagina.titel" /></span></a></td></tr><% 
			} %></mm:field
			><%--
			
			lets look whether there are subpages under this page
			--%><mm:first
				><mm:list nodes="<%= rubriek_number %>" path="rubriek1,parent,rubriek2,posrel,pagina"
				orderby="parent.pos,posrel.pos" directions="UP,UP"
				fields="pagina.titel,pagina.number"
				><tr><td>
					&nbsp;&nbsp;&nbsp;
					<a href=<%@include file="..\includes\page2url.jsp" 
						%>><span class="normal"><mm:field name="pagina.titel" /></span></a>
				</td></tr>
			</mm:list
			></mm:first
			><mm:last
				></table>
				<img src="media/spacer.gif" width="225" height="10"></mm:last
		></mm:related
		></mm:node
		></mm:field><%
	} 
	%></mm:field
></mm:list>
	