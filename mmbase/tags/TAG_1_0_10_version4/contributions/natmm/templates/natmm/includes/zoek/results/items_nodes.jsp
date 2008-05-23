<%
   HashSet hsetItemsNodes = 
      su.addPages(cloud,cf,qStr,8,"items,posrel,pagina",subsiteID,nowSec,hsetPagesNodes); 
   
   for (Iterator iter1 = hsetItemsNodes.iterator(); iter1.hasNext(); ) {
      String sItemId = (String) iter1.next(); 
      String sTitel = "";
      String sRubriek = "";
      String sPagina = "";
      
      %><mm:node number="<%=sItemId%>">
         <mm:field name="titel" jspvar="stitel" vartype="String" write="false"><%sTitel = stitel;%></mm:field>
      
         <mm:related path="posrel,pagina,posrel,rubriek" fields="pagina.number" searchdir="source">
            <mm:field name="pagina.titel" jspvar="spagina" vartype="String" write="false"><%sPagina = spagina;%></mm:field>            
            <mm:field name="rubriek.naam" jspvar="srubriek" vartype="String" write="false"><%sRubriek = srubriek;%></mm:field>         
         
            <mm:field name="pagina.number" jspvar="sPageId" vartype="String" write="false">
            <%
               templateUrl = ph.createPaginaUrl(sPageId,request.getContextPath());
               templateUrl += (templateUrl.indexOf("?") ==-1 ? "?" : "&");
                  
                  searchResults.add("<ul><li>"
                        + "<a href=\"" + templateUrl + "u=" + sItemId + "\">" + sTitel + "</a><br/>"
                        + "<span class=\"colortitle\">" + sRubriek + "</span> - <b>" + sPagina + "</b>"
                        + "</li></ul><br/>");
            %>
            </mm:field>
         </mm:related>
                  
      </mm:node><%
   }
%>