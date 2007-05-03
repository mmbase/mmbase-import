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
          <option><%= MetaDataHelper.EMPTY_VALUE %></option>
          <mm:related path="posrel,metavocabulary" searchdir="destination" orderby="posrel.pos">
             <mm:node element="metavocabulary" jspvar="nodeMetaVocabulary">
                <%
                   if(MetaDataHelper.isTheMetaVocabularyActive(nodeMetaVocabulary, nodeObject, thisMetadefinition, sMetastandartNodes, application)){
                      %>
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
                               <%= MetaDataHelper.getAliasForObject(cloud, sMetavocabularyID, nodeUser.getNumber()) %>
                            </mm:field>
                            </option>
                         </mm:field>
                      <%
                   }
                %>
             </mm:node>
          </mm:related>
      </select>

      <%
      session.setAttribute("metaeditor_multilevel_metavocabulary_all_metadata", nlRelatedNodes);
      if(!"".equals(sSelected)){
         %>
            <jsp:include page="metaedit_form_vocabulary_sublevel.jsp" flush="true">
               <jsp:param name="vocabulary" value="<%= sSelected %>" />
               <jsp:param name="metadefinition" value="<%= thisMetadefinition.getNumber() %>" />
               <jsp:param name="metastandarts" value="<%= sMetastandartNodes %>" />
               <jsp:param name="object" value="<%= sNode %>" />
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
      <mm:related path="posrel,metavocabulary" searchdir="destination" orderby="posrel.pos">
         <mm:node element="metavocabulary" jspvar="nodeMetaVocabulary">
            <%
               if(MetaDataHelper.isTheMetaVocabularyActive(nodeMetaVocabulary, nodeObject, thisMetadefinition, sMetastandartNodes, application)){
                  %>
                     <mm:field name="number" jspvar="sID" vartype="String" write="false">
                        <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sID %>" checkbox_id="<%= nodeMetaVocabulary.getNumber() %>" onClick="switchMetaVocabularyTree(this)"
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
                           <%= MetaDataHelper.getAliasForObject(cloud, sMetavocabularyID, nodeUser.getNumber()) %>
                        </mm:field>
                     </mm:field>
                  <%

                  session.setAttribute("metaeditor_multilevel_metavocabulary_all_metadata", nlRelatedNodes);

                  %>
                     <jsp:include page="metaedit_form_vocabulary_sublevel.jsp" flush="true">
                        <jsp:param name="vocabulary" value="<%= nodeMetaVocabulary.getStringValue("number") %>" />
                        <jsp:param name="metadefinition" value="<%= thisMetadefinition.getNumber() %>" />
                        <jsp:param name="blocked" value="<%= bBlocked %>" />
                        <jsp:param name="metastandarts" value="<%= sMetastandartNodes %>" />
                        <jsp:param name="object" value="<%= sNode %>" />
                     </jsp:include>
                  <%
               }
            %>
         </mm:node>
      </mm:related>
   <%
}
%>
