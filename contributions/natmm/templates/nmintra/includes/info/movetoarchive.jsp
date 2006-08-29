<%
// *** delete expired articles from this page (if it is not the archive) ***
boolean isArchive = false;
%><mm:node number="<%= paginaID %>"
   ><mm:aliaslist
      ><mm:write jspvar="alias" vartype="String" write="false"><%
         isArchive = (alias.indexOf("archief") > -1); 
      %></mm:write
   ></mm:aliaslist><%
%></mm:node><%
if(!isArchive) {
   // move expired artikelen to archive or delete node
   String articleConstraint = "(artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
   %><mm:list nodes="<%= paginaID %>" path="pagina1,readmore,pagina2"
      ><mm:node element="pagina2" id="archive"
      ><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
	    	orderby="artikel.embargo" searchdir="destination" 
         constraints="<%= "!( " + articleConstraint + " )" %>"
         	><mm:deletenode element="contentrel" 
            /><mm:node element="artikel" id="thisarticle"
               ><mm:field name="archive"
                  ><mm:compare value="no"
                     ><mm:relatednodes type="paragraaf"
                        ><mm:deletenode 
                     /></mm:relatednodes
                     ><mm:deletenode
                     /></mm:compare
                  ><mm:compare value="no" inverse="true"
                     ><mm:createrelation source="archive" destination="thisarticle" role="contentrel" 
                  /></mm:compare
              ></mm:field
            ></mm:node
            ><mm:remove referid="thisarticle" 
		/></mm:list
   ></mm:node
  ></mm:list><% 
} else {
   // check if artikelen should be removed from archive
   String articleConstraint = "(artikel.archive = 'half_year' AND artikel.verloopdatum < '" + nowSec + "' )";
   %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
    	orderby="artikel.embargo" searchdir="destination" 
      constraints="<%= articleConstraint %>"
   	><mm:deletenode element="contentrel" 
      /><mm:node element="artikel"
         ><mm:relatednodes type="paragraaf"
            ><mm:deletenode 
         /></mm:relatednodes
         ><mm:deletenode
      /></mm:node
   ></mm:list><%
}
%>