<%
/*
delete expired articles from this page (if it is not the archive)
all archives should have an oalias with "archief" as substring
*/
boolean isArchive = false;
%><mm:node number="<%= paginaID %>"
   ><mm:aliaslist
      ><mm:write jspvar="alias" vartype="String" write="false"><%
         isArchive = (alias.indexOf("archief") > -1); 
      %></mm:write
   ></mm:aliaslist><%
%></mm:node><%
if(!isArchive) {
   /* 
   move expired artikelen to archive or artikel node and related paragraphs
   note: related images, attachments and links are not deleted
   pages can have their individual archives by using a pagina,readmore,pagina relationship
   if this relation is not present the general archive with oalias "archief" is used
   */
   %>
   <mm:node number="<%= paginaID %>">
      <mm:related path="readmore,pagina" searchdir="destination">
         <mm:node number="archief" id="archive" />
      </mm:related>
      <mm:notpresent referid="archive">
         <mm:node number="archief" id="archive" />
      </mm:notpresent>
      <mm:present referid="archive">
         <%
         String articleConstraint = "(artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
         %><mm:related path="contentrel,artikel" 
   	    	orderby="artikel.embargo" searchdir="destination" 
            constraints="<%= "!( " + articleConstraint + " )" %>"
            	><mm:deletenode element="contentrel" 
               /><mm:node element="artikel" id="thisarticle"
                  ><mm:field name="archive"
                     ><mm:compare value="no"
                        ><mm:relatednodes type="paragraaf"
                           ><mm:deletenode deleterelations="true"
                        /></mm:relatednodes
                        ><mm:deletenode deleterelations="true"
                        /></mm:compare
                     ><mm:compare value="no" inverse="true"
                        ><mm:createrelation source="archive" destination="thisarticle" role="contentrel" 
                     /></mm:compare
                 ></mm:field
               ></mm:node
               ><mm:remove referid="thisarticle" 
   		/></mm:related>
      </mm:present>
   </mm:node><% 
} else {
   // check if artikelen should be removed from archive
   String articleConstraint = "(artikel.archive = 'half_year' AND artikel.verloopdatum < '" + nowSec + "' )";
   %><mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" 
    	orderby="artikel.embargo" searchdir="destination" 
      constraints="<%= articleConstraint %>"
   	><mm:deletenode element="contentrel" 
      /><mm:node element="artikel"
         ><mm:relatednodes type="paragraaf"
            ><mm:deletenode deleterelations="true"
         /></mm:relatednodes
         ><mm:deletenode deleterelations="true"
      /></mm:node
   ></mm:list><%
}
%>