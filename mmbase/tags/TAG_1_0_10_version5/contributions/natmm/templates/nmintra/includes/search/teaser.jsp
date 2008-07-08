<mm:related path="contentrel,teaser">
   <mm:field name="teaser.number" jspvar="sID" vartype="String" write="false"><%
     if(hsetTeaserNodes.contains(sID)){
       %><mm:field name="teaser.titel" jspvar="titel" vartype="String" write="false">
          <li><a href="<%= templateUrl %>?p=<%=sPageID%>">
              <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
        </mm:field>	
        <mm:field name="teaser.omschrijving" jspvar="dummy" vartype="String" write="false">
          <% textStr = dummy; %>
        </mm:field>
        <br/><%= su.highlightSearchTerms(textStr,defaultSearchTerms,"b") %>
        <% 
     } %>
  </mm:field>
</mm:related>
