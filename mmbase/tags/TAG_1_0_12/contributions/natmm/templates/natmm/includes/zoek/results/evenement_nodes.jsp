<%
   HashSet hsetEvenementNodes = 
      su.addPages(cloud,cf,qStr,4,null,subsiteID,nowSec,hsetPagesNodes);

   String sTitel2 = "";
   String sRubriek2 = "";
   String sPagina2 = "";
   
   %><mm:node number="agenda">
      <mm:list nodes="agenda" path="pagina,posrel,rubriek">
         <mm:field name="rubriek.naam" jspvar="srubriek" vartype="String" write="false"><%sRubriek2 = srubriek;%></mm:field>
      </mm:list>
   
      <mm:field name="titel" jspvar="spagina" vartype="String" write="false"><%sPagina2 = spagina;%></mm:field>   
      <mm:field name="number" jspvar="agenda_number" vartype="String" write="false">
      <%
         templateUrl = ph.createPaginaUrl(agenda_number,request.getContextPath());
         templateUrl += (templateUrl.indexOf("?") ==-1 ? "?" : "&");
      %>                     
      </mm:field>
   </mm:node><%
   
   for (Iterator iter2 = hsetEvenementNodes.iterator(); iter2.hasNext(); ) {
      String sEvenementId = (String) iter2.next();
      %><mm:node number="<%= sEvenementId %>">
         <mm:field name="titel" jspvar="stitel" vartype="String" write="false"><%sTitel2 = stitel;%></mm:field>
         <%
            searchResults.add("<ul><li>"
                  + "<a href=\"" + templateUrl + "e=" + Evenement.getNextOccurence(sEvenementId) + "\">" + sTitel2 + "</a><br/>"
                  + "<span class=\"colortitle\">" + sRubriek2 + "</span> - <b>" + sPagina2 + "</b>"
                  + "</li></ul><br/>");         
         %>
      </mm:node><%
   }       
%>