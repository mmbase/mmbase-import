<%
if(!isArchive) { 
  long month_time = 31*24*60*60;
  %><mm:list nodes="<%= paginaID %>" path="pagina1,readmore,pagina2"
      ><mm:node element="pagina2" id="archive"
      ><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
	    orderby="artikel.embargo" searchdir="destination" 
          constraints="<%= "( artikel.embargo < " + (nowSec-month_time) + ")"
                 + " OR ( (artikel.verloopdatum != '0') AND (artikel.verloopdatum < " + nowSec + "))"        
           %>"
         ><mm:field name="contentrel.pos" jspvar="posrel_number" vartype="String" write="false"><%
            if(posrel_number==null||!posrel_number.equals("0")) { 
               %><mm:deletenode element="contentrel" 
               /><mm:node element="artikel" id="thisarticle"
               /><mm:createrelation source="archive" destination="thisarticle" role="contentrel" 
               /><mm:remove referid="thisarticle" /><%
            }
         %></mm:field
      ></mm:list
   ></mm:node
  ></mm:list><% 
}
%>