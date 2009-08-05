<mm:related path="readmore,contentblocks">
  <mm:field name="contentblocks.number" jspvar="sID" vartype="String" write="false">
    <%
    if(hsetContentBlockNodes.contains(sID)){
      %>
      <mm:node element="contentblocks">
        <mm:related path="readmore,attachments" fields="attachments.number">
          <%@include file="show_attachments.jsp" %>
        </mm:related>
        <mm:field name="subtitle" jspvar="dummy" vartype="String" write="false">
           <%
           textStr = dummy;
           %>
        </mm:field>
        <mm:field name="description" jspvar="dummy" vartype="String" write="false">
           <% 
           if (!textStr.equals("")) { textStr += " "; }
           textStr += dummy; 
           %>
        </mm:field>
        <mm:field name="title" jspvar="titel" vartype="String" write="false">
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
