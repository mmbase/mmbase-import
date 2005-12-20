<%
if(sMaxValues.equals("1"))
{
   String sSelected = "";
   %>
      <mm:relatednodes type="metadata" jspvar="mNode">
         <%
            if(nlRelatedNodes.contains(mNode))
            {
               %>
                  <mm:relatednodes type="metavocabulary" orderby="value">
                     <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                        <%
                           sSelected = sValue;
                        %>
                     </mm:field>
                  </mm:relatednodes>
               <%
            }
         %>
      </mm:relatednodes>
      <select name="m<mm:field name="number"/>">
          <option><%= mdh.EMPTY_VALUE %></option>
          <mm:relatednodes type="metavocabulary" orderby="value">
             <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
                <option name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
                   <%
                      if(sSelected.equals(sCurrent))
                      {
                         %> selected="selected" <%
                      }
                   %>
                ><%= sCurrent %></option>
             </mm:field>
          </mm:relatednodes>
      </select>
   <%
}
else
{
   HashSet hsetSelected = new HashSet();
   %>
      <mm:relatednodes type="metadata" jspvar="mNode">
         <%
            if(nlRelatedNodes.contains(mNode))
            {
               %>
                  <mm:relatednodes type="metavocabulary" orderby="value">
                     <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                        <%
                           hsetSelected.add(sValue);
                        %>
                     </mm:field>
                  </mm:relatednodes>
               <%
            }
         %>
      </mm:relatednodes>
      <mm:relatednodes type="metavocabulary" orderby="value">
         <mm:field name="value" jspvar="sCurrent" vartype="String" write="false">
            <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sCurrent %>"
            <%
               if(hsetSelected.contains(sCurrent))
               {
                  %> checked="checked" <%
               }
            %>
            /><%= sCurrent %>
            <br/>
         </mm:field>
      </mm:relatednodes>
   <%
}
%>