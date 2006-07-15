<%
if(!isArchive) { 
  %><mm:list nodes="<%= paginaID %>" path="pagina1,readmore,pagina2"
      ><mm:node element="pagina2" id="archive"
      ><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
	    	orderby="artikel.embargo" searchdir="destination" 
         constraints="<%= "(artikel.verloopdatum != '0') AND (artikel.verloopdatum < " + nowSec + ")" %>"
         	><mm:deletenode element="contentrel" 
            /><mm:node element="artikel" id="thisarticle"
            /><mm:createrelation source="archive" destination="thisarticle" role="contentrel" 
            /><mm:remove referid="thisarticle" 
		/></mm:list
   ></mm:node
  ></mm:list><% 
}
%>