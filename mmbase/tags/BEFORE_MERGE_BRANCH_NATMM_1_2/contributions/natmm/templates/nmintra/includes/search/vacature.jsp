<mm:related path="contentrel,vacature">
  <mm:field name="vacature.number" jspvar="sID" vartype="String" write="false">
    <%  
    if(hsetVacatureNodes.contains(sID)){
      %>
      <mm:node element="vacature">
        <mm:related path="posrel,attachments" fields="attachments.number">
            <%@include file="show_attachments.jsp" %>
        </mm:related>
        <%
        LinkedList ll = new LinkedList();
        ll.add("functienaam"); 
        ll.add("omschrijving");	
        ll.add("functieinhoud"); 
        ll.add("functieomvang"); 
        ll.add("duur"); 
        ll.add("afdeling"); 
        ll.add("functieeisen"); 
        ll.add("opleidingseisen"); 
        ll.add("competenties"); 
        ll.add("salarisschaal");
        ll.add("metatags");  
        Iterator itl = ll.iterator();
        textStr = "";
        while (itl.hasNext()){ %>
          <mm:field name="<%= (String)itl.next() %>" jspvar="dummy1" vartype="String" write="false">
             <% 
             if (dummy1!=null) {
               if (!textStr.equals("")) { textStr += " "; }
               textStr += dummy1; 
             } %>
           </mm:field>
           <%	
        } %>
        <mm:field name="titel" jspvar="titel" vartype="String" write="false">
          <% 
          String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
          if (!highlightSearchTerms.trim().equals("")){
            highlightSearchTerms += "<br/>";
          }
          if (bHasAttachments) {%>
            <%= highlightSearchTerms %>
            <a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="number"/>">
            <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
            <% 
          } else {
            %>
            <li><a href="<%= templateUrl %>?p=<%=sPageID%>&project=<mm:field name="number"/>">
            <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
            <%= highlightSearchTerms %>
            <% 
          } %>
          <br/>	
       </mm:field>
     </mm:node>
     <%
   }
   bHasAttachments = false;
   %>
   </mm:field>
</mm:related>

