<mm:related path="posrel,items">
  <mm:field name="items.number" jspvar="sID" vartype="String" write="false">
    <mm:list nodes="<%= sID %>" path="items,posrel,attachments" fields="attachments.number,attachments.filename,attachments.titel">
      <mm:field name="attachments.number" jspvar="sAttID" vartype="String" write="false">
        <% if (hsetAttachmentsItemsNodes.contains(sAttID)) { 
          bHasAttachments = true; %>
        <%@include file="show_attachments.jsp" %>
        <%	}%>
      </mm:field>
    </mm:list><%
    if(hsetItemsNodes.contains(sID)||bHasAttachments){
      %><mm:field name="items.intro" jspvar="dummy" vartype="String" write="false">
         <%
         textStr = dummy;
         %>
       </mm:field>
       <mm:field name="items.body" jspvar="dummy" vartype="String" write="false">
         <% 
         if (!textStr.equals("")) { textStr += " "; }
         textStr += dummy; 
         %>
       </mm:field>
       <mm:field name="items.titel" jspvar="titel" vartype="String" write="false">
         <% 
         String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
         if (!highlightSearchTerms.trim().equals("")){
           highlightSearchTerms += "<br/>";
         }
         if (bHasAttachments) {%>
           <%= highlightSearchTerms %>
           <a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
           <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
           <% 
         } else {%>	
           <li><a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="items.number"/>">
           <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
           <%= highlightSearchTerms %>
           <% 
         } 
         %><br/>
       </mm:field>	
       <% 
     }
     bHasAttachments = false;
     %>
  </mm:field>
</mm:related>
