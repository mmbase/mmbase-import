<%// Get languages list %>
<mm:listnodes type="metastandard" >
   <mm:relatednodes type="metadefinition" role="posrel">
      <mm:field name="handler" jspvar="sHandler" vartype="String" write="false">
         <mm:field name="number" jspvar="sID" vartype="String" write="false">
            <%
               if(sHandler.equals("taal")){
                  %>
                  <mm:node number="<%= sID %>">
                     <mm:relatednodes type="metavocabulary">
                        <mm:field name="value" jspvar="sLang" vartype="String">
                           <%
                           hsetLangCodes.add(sLang);
                           %>
                        </mm:field>
                     </mm:relatednodes>
                  </mm:node>
               <%
               }
            %>
         </mm:field>
      </mm:field>
   </mm:relatednodes>
</mm:listnodes>
