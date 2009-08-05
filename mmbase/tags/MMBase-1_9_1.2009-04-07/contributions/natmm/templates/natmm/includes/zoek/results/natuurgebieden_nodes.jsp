<%
HashSet hsetNatuurgebiedenNodes = 
   su.addPages(cloud,cf,qStr,2,"natuurgebieden,pos4rel,provincies,contentrel,pagina",subsiteID,nowSec,hsetPagesNodes);
   
   for (Iterator iter3 = hsetNatuurgebiedenNodes.iterator(); iter3.hasNext(); ) {
      String sNatuurGebiedId = (String) iter3.next(); 
      String sTitel = "";
      String sRubriek = "";
      String sPagina = "";
      
      %><mm:node number="<%=sNatuurGebiedId%>">
         <mm:field name="titel" jspvar="stitel" vartype="String" write="false"><%sTitel = stitel;%></mm:field>

         <mm:related path="pos4rel,provincies,contentrel,pagina,posrel,rubriek" fields="pagina.number" searchdir="source">
            <mm:field name="pagina.titel" jspvar="spagina" vartype="String" write="false"><%sPagina = spagina;%></mm:field>            
            <mm:field name="rubriek.naam" jspvar="srubriek" vartype="String" write="false"><%sRubriek = srubriek;%></mm:field>
                              
            <mm:field name="pagina.number" jspvar="sPageId" vartype="String" write="false">
            <%
               templateUrl = ph.createPaginaUrl(sPageId,request.getContextPath());
               templateUrl += (templateUrl.indexOf("?") ==-1 ? "?" : "&");
               
               searchResults.add("<ul><li>"
                     + "<a href=\"" + templateUrl + "n=" + sNatuurGebiedId + "\">" + sTitel + "</a><br/>"
                     + "<span class=\"colortitle\">" + sRubriek + "</span> - <b>" + sPagina + "</b>"
                     + "</li></ul><br/>");
            %>
            </mm:field>
         </mm:related>
                  
      </mm:node><%
   }
%>