 <mm:related path="contentrel,vraagbaak" fields="vraagbaak.number" orderby="vraagbaak.begindatum" directions="DOWN">
   <mm:field name="vraagbaak.number" jspvar="sID" vartype="String" write="false">
     <%
     if(hsetVraagbaakNodes.contains(sID)){
       %>
       <mm:node element="vraagbaak" id="this_article">
         <mm:related path="posrel,paragraaf,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
            <%@include file="show_attachments.jsp" %>															
         </mm:related>
         <mm:field name="titel" jspvar="titel" vartype="String" write="false">
           <%@include file="highlightsshow.jsp" %>
           <% 
           String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
           if (!highlightSearchTerms.trim().equals("")) {
             highlightSearchTerms += "<br/>";
           }
           titleStr = titel;
           if (bHasAttachments) {%>
             <%@include file="../poolanddate.jsp" %>
             <%= highlightSearchTerms %>
             <a href="<%= templateUrl %>?p=<%=sPageID%>">
             <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
             <% 
           } else {
             %>
             <li><a href="<%= templateUrl %>?p=<%=sPageID%>&vraagbaak=<%= sID %>">
             <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
             <%@include file="../poolanddate.jsp" %>
             <%= highlightSearchTerms %>
             <% 
            } %><br/>	
         </mm:field>
       </mm:node>
       <mm:remove referid="this_article" />
       <% 
     }
     bHasAttachments = false;
     %>
  </mm:field>
</mm:related>
