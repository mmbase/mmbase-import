<%
   HashSet hsetArticlesNodes = 
      su.addPages(cloud,cf,qStr,0,"artikel,contentrel,pagina",subsiteID,nowSec,hsetPagesNodes);
    
   for (Iterator iter0 = hsetArticlesNodes.iterator(); iter0.hasNext(); ) {
      String sArticleId = (String) iter0.next(); 
      String sTitel = "";
      String sRubriek = "";
      String sPagina = "";
      
      %><mm:node number="<%=sArticleId%>">  
         <mm:field name="titel" jspvar="stitel" vartype="String" write="false"><%sTitel = stitel;%></mm:field>
      
         <mm:related path="contentrel,pagina,posrel,rubriek" fields="pagina.number" searchdir="source">            
            <mm:field name="pagina.titel" jspvar="spagina" vartype="String" write="false"><%sPagina = spagina;%></mm:field>            
            <mm:field name="rubriek.naam" jspvar="srubriek" vartype="String" write="false"><%sRubriek = srubriek;%></mm:field>
            
            <mm:field name="pagina.number" jspvar="sPageId" vartype="String" write="false">    
            <%
               templateUrl = ph.createPaginaUrl(sPageId,request.getContextPath());
               templateUrl += (templateUrl.indexOf("?") ==-1 ? "?" : "&");
            
               searchResults.add("<ul><li>"
                  + "<a href=\"" + templateUrl + "id=" + sArticleId + "\">" + sTitel + "</a><br/>"
                  + "<span class=\"colortitle\">" + sRubriek + "</span> - <b>" + sPagina + "</b>"
                  + "</li></ul><br/>");
            %>
            </mm:field>
         </mm:related>
                  
      </mm:node><%
   }
%>