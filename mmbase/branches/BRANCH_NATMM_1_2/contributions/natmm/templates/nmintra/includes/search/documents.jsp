<mm:related path="posrel,documents">
   <mm:field name="documents.number" jspvar="sID" vartype="String" write="false"><%
      if(hsetDocumentsNodes.contains(sID)){
          %><mm:field name="documents.filename" jspvar="titel" vartype="String" write="false">
            <li><a href="<mm:field name="documents.url"/>">
                <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
         </mm:field>		
          <%
      }
   %></mm:field>
</mm:related>
