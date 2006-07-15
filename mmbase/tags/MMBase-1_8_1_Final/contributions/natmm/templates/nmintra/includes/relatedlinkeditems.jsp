<mm:list nodes="<%= paginaID %>" path="<%= "pagina1,readmore," + targetObject %>" searchdir="destination" orderby="<%= targetObject + ".titel" %>"
	><mm:field name="dreadmore.readmore" jspvar="coordinates" vartype="String" write="false"
	   ><a href="<mm:field name="<%= targetObject + ".number" %>" jspvar="number" vartype="String" write="false"><%
   			 if(targetObject.equals("pagina2")) { // *** jump to another page ***
      			%><%= readmoreUrl + number %><%
      	    } else{ // *** jump to the same page ***
      			%><%= readmoreUrl + number %><%
      		 } 
   	   %></mm:field
            >" style="padding-left:10px;"><mm:field name="<%= targetObject + ".titel" %>" /></a><br/>
   </mm:field
></mm:list>