<mm:list nodes="<%= paginaID %>" path="<%= "pagina1,posrel,images,pos4rel," + targetObject %>" searchdir="destination"
	><area alt="<mm:field name="<%= targetObject + ".titel" %>"
		/>" shape="RECT" coords="<mm:field name="pos4rel.pos1" />,<mm:field name="pos4rel.pos2" />,<mm:field name="pos4rel.pos3" />,<mm:field name="pos4rel.pos4" 
		/>" href="<mm:field name="<%= targetObject + ".number" %>" jspvar="number" vartype="String" write="false"><%
			 if(targetObject.equals("pagina2")) { // *** jump to another page ***
				%><%= ph.createPaginaUrl(number,request.getContextPath()) %><%
			} else{ // *** jump to the same page ***
				%><%= readmoreUrl + number %><%
			}
			%></mm:field>">
</mm:list>