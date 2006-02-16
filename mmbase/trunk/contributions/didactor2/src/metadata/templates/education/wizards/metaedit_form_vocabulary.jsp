<%
boolean bBlocked = false;

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
      session.setAttribute("metaeditor_multilevel_metavocabulary_all_metadata", nlRelatedNodes);
      session.setAttribute("metaeditor_multilevel_metavocabulary_metadatahelper", mdh);
      if(!"".equals(sSelected)){
         %>
            <jsp:include page="metaedit_form_vocabulary_sublevel.jsp" flush="true">
               <jsp:param name="vocabulary" value="<%= sSelected %>" />
               <jsp:param name="metadefinition" value="<%= thisMetadefinition.getNumber() %>" />
            </jsp:include>
         <%
      }
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
      <mm:related path="metavocabulary" orderby="metavocabulary.value">
         <mm:node element="metavocabulary" jspvar="nodeMetavocabulary">
            <mm:field name="number" jspvar="sID" vartype="String" write="false">
               <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sID %>" checkbox_id="<%= nodeMetavocabulary.getNumber() %>" onClick="switchMetaVocabularyTree(this)"
               <%

                  if(hsetSelected.contains(sID)){
                     %> checked="checked" <%
                     bBlocked = false;
                  }
                  else{
                     bBlocked = true;
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

            <%
               session.setAttribute("metaeditor_multilevel_metavocabulary_all_metadata", nlRelatedNodes);
               session.setAttribute("metaeditor_multilevel_metavocabulary_metadatahelper", mdh);
            %>
            <jsp:include page="metaedit_form_vocabulary_sublevel.jsp" flush="true">
               <jsp:param name="vocabulary" value="<%= nodeMetavocabulary.getStringValue("number") %>" />
               <jsp:param name="metadefinition" value="<%= thisMetadefinition.getNumber() %>" />
               <jsp:param name="blocked" value="<%= bBlocked %>" />
            </jsp:include>
         </mm:node>
      </mm:related>
   <%
}
%>


