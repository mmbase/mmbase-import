<mm:related path="posrel,items">
  <mm:field name="items.number" jspvar="sID" vartype="String" write="false">
    <%
    if(hsetItemsNodes.contains(sID)){
      %>
      <mm:node element="items">
        <mm:related path="posrel,attachments" fields="attachments.number">
          <%@include file="show_attachments.jsp" %>
        </mm:related>
        <mm:field name="intro" jspvar="dummy" vartype="String" write="false">
           <%
           textStr = dummy;
           %>
        </mm:field>
        <mm:field name="body" jspvar="dummy" vartype="String" write="false">
           <% 
           if (!textStr.equals("")) { textStr += " "; }
           textStr += dummy; 
           %>
        </mm:field>
        <mm:field name="titel" jspvar="titel" vartype="String" write="false">
           <% 
           String highlightSearchTerms = su.highlightSearchTerms(textStr,defaultSearchTerms,"b");
           if (!highlightSearchTerms.trim().equals("")){
             highlightSearchTerms += "<br/>";
           }
           if (bHasAttachments) {
             %>
             <%= highlightSearchTerms %>
             <a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="number"/>">
             <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a><br/>
             <% 
           } else {
             %>	
             <li><a href="<%= templateUrl %>?p=<%=sPageID%>&u=<mm:field name="number"/>">
             <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li><br/>
             <%= highlightSearchTerms %>
             <% 
           } 
           %><br/>
         </mm:field>
       </mm:node>
       <% 
     }
     bHasAttachments = false;
     %>
  </mm:field>
</mm:related>
