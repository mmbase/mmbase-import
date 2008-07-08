<mm:related path="posrel,producttypes" fields="producttypes.number">
  <mm:field name="producttypes.number" jspvar="sID" vartype="String" write="false"><%
     if(hsetProducttypesNodes.contains(sID)){
        %><mm:field name="producttypes.titel" jspvar="titel" vartype="String" write="false">
          <li><a href="<%= templateUrl %>?p=<%=sPageID%>&pool=<mm:field name="producttypes.number"/>">
          <span class="normal" style="text-decoration:underline;"><%= su.highlightSearchTerms(titel,defaultSearchTerms,"b") %></span></a></li>
        </mm:field>	
        <%	
     }
  %></mm:field>
</mm:related>
