<mm:list nodes="<%= paginaID %>" path="<%= "pagina1,posrel,images,pos4rel," + targetObject %>" 
	><mm:field name="pos4rel.pos1" jspvar="x1" vartype="Integer" write="false"
	><mm:field name="pos4rel.pos2" jspvar="y1" vartype="Integer" write="false"
	><mm:field name="pos4rel.pos3" jspvar="x2" vartype="Integer" write="false"
	><mm:field name="pos4rel.pos4" jspvar="y2" vartype="Integer" write="false"
	><div style="position:absolute;left:<%= x1 %>px;top:<%= y1 
				%>px;width:<%= x2.intValue()-x1.intValue() %>px;height:<%= y2.intValue()-y1.intValue() 
				%>px;text-align:center;font-size:11px;border:dashed thin #FF0000;color:#FF0000;">
		<a href="<mm:field name="<%= targetObject + ".number" %>" jspvar="number" vartype="String" write="false"><%
			 if(targetObject.equals("pagina2")) { // *** jump to another page ***
				%><mm:url page="<%=  ph.createPaginaUrl(number,request.getContextPath()) %>" /><%
			} else{ // *** jump to the same page ***
				%><mm:url page="<%= readmoreUrl + number %>" /><%
			}
			%></mm:field>" alt="<mm:field name="<%= targetObject + ".titel" %>" 
			/>" style="color:#FF0000;text-decoration:none;"><mm:field name="<%= targetObject + ".titel" %>" /></a></div>
	</mm:field
	></mm:field
	></mm:field
	></mm:field	
></mm:list>