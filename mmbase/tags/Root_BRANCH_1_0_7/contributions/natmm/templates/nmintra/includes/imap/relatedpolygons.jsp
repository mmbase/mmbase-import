<mm:list nodes="<%= paginaID %>" path="<%= "pagina1,readmore," + targetObject %>" searchdir="destination"
	><mm:field name="readmore.readmore" jspvar="coordinates" vartype="String" write="false"><%
	   coordinates += "+"; 
	   int pPos = coordinates.indexOf("+");
	   while(pPos>-1) {
	      String thisCoordinates = coordinates.substring(0,pPos);
	      coordinates = coordinates.substring(pPos+1);
	      pPos = coordinates.indexOf("+");
	      %><area alt="<mm:field name="<%= targetObject + ".titel" %>"
      		/>" coords="<%= thisCoordinates 
      		%>" href="<mm:field name="<%= targetObject + ".number" %>" jspvar="number" vartype="String" write="false"><%
         			 if(targetObject.equals("pagina2")) { // *** jump to another page ***
            			%><%= readmoreUrl + number %><%
            	    } else{ // *** jump to the same page ***
            			%><%= readmoreUrl + number %><%
            		 } 
         	   %></mm:field
            >" shape="POLYGON"><%
      }
   %></mm:field>
</mm:list>