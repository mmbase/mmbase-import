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
                     <mm:field name="number" jspvar="sID" vartype="String" write="false">
                        <%
                           sSelected = sID;
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
             <mm:field name="number" jspvar="sID" vartype="String" write="false">
                <option name="m<%= sMetaDefinitionID %>" value="<%= sID %>"
                   <%
                      if(sSelected.equals(sID))
                      {
                         %> selected="selected" <%
                      }
                   %>
                >
                <mm:field name="number" jspvar="sMetavocabularyID" vartype="String">
                   <mm:node number="$user" jspvar="nodeUser">
                      <%= mdh.getAliasForObject(cloud, sMetavocabularyID, nodeUser.getNumber()) %>
                   </mm:node>
                </mm:field>
                </option>
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
                     <mm:field name="number" jspvar="sID" vartype="String" write="false">
                        <%
                           hsetSelected.add(sID);
                        %>
                     </mm:field>
                  </mm:relatednodes>
               <%
            }
         %>
      </mm:relatednodes>
      <mm:relatednodes type="metavocabulary" orderby="value">
         <mm:field name="number" jspvar="sID" vartype="String" write="false">
            <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sID %>"
            <%
               if(hsetSelected.contains(sID))
               {
                  %> checked="checked" <%
               }
            %>
            />
            <mm:field name="number" jspvar="sMetavocabularyID" vartype="String">
               <mm:node number="$user" jspvar="nodeUser">
                  <%= mdh.getAliasForObject(cloud, sMetavocabularyID, nodeUser.getNumber()) %>
               </mm:node>
            </mm:field>
            <br/>
         </mm:field>
      </mm:relatednodes>
   <%
}
%>