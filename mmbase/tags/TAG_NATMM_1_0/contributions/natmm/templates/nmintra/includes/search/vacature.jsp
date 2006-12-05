<mm:related path="contentrel,vacature">
  <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false">
    <mm:list nodes="<%= sID %>" path="vacature,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
      <mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
        <% if (hsetAttachmentsVacaturesNodes.contains(sAttID)) { 
          bHasAttachments = true; %>
        <%@include file="show_attachments.jsp" %>
        <%	}%>
      </mm:field>
    </mm:list>
    <%  
    if(hsetVacatureNodes.contains(sID)||bHasAttachments){
      LinkedList ll = new LinkedList();
      ll.add("vacature.functienaam"); 
      ll.add("vacature.omschrijving");	
      ll.add("vacature.functieinhoud"); 
      ll.add("vacature.functieomvang"); 
      ll.add("vacature.duur"); 
      ll.add("vacature.afdeling"); 
      ll.add("vacature.functieeisen"); 
      ll.add("vacature.opleidingseisen"); 
      ll.add("vacature.competenties"); 
      ll.add("vacature.salarisschaal");
      ll.add("vacature.metatags");  
      Iterator itl = ll.iterator();
      textStr = "";
      while (itl.hasNext()){ %>
        <mm:field name="<%= (String)itl.next() %>" jspvar="dummy1" vartype="String" write="false">
         <% if (dummy1!=null) {
                if (!textStr.equals("")) { textStr += " "; }
               textStr += dummy1; 
             } %>
         </mm:field>
      <%	
      } %>
      <mm:field name="vacature.titel" jspvar="titel" vartype="String" write="false">
        <% 
        String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
        if (!highlightSearchTerms.trim().equals("")){
          highlightSearchTerms += "<br/>";
        }
        if (bHasAttachments) {%>
          <%= highlightSearchTerms %>
          <a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
          <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
          <% 
        } else {
          %>
          <li><a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="vacature.number"/>">
          <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
          <%= highlightSearchTerms %>
          <% 
        } %>
        <br/>	
     </mm:field>	
     <% 
   }
   bHasAttachments = false;
   %>
   </mm:field>
</mm:related>

