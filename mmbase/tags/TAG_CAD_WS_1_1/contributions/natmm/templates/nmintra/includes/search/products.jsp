<mm:related path="posrel,producttypes,posrel,products">
   <mm:field name="products.number" jspvar="sID" vartype="String" write="false"><%
     if(hsetProductsNodes.contains(sID)){
        %><mm:field name="products.titel" jspvar="titel" vartype="String" write="false">
            <li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>&product=<mm:field name="products.number"/>">
              <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
        </mm:field>	
        <mm:field name="products.omschrijving" jspvar="dummy" vartype="String" write="false">
          <% textStr = dummy; %>
        </mm:field>
        <br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
        <% 
      } %>
  </mm:field>
</mm:related>
