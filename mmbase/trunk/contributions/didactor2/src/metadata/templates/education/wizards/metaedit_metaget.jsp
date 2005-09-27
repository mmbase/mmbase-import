<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:cloud>
   <%

      String sNode = request.getParameter("node");
      String sMetadataDefinitionID = request.getParameter("metadata_definition");

      boolean bTemplatesActive = false;
      if (request.getParameter("set_defaults") == null) bTemplatesActive = true;

      String sMetadataID = null;
      %>
         <mm:node number="<%= sNode %>">
            <mm:related path="metadata,metadefinition">
               <mm:field name="metadefinition.number" jspvar="sMetadefID" vartype="String">
                  <mm:field name="metadata.number" jspvar="sMetadatID" vartype="String">
                     <%
                        if (sMetadefID.equals(sMetadataDefinitionID))
                        {
                           sMetadataID = sMetadatID;
                        }
                     %>
                  </mm:field>
               </mm:field>
            </mm:related>
         </mm:node>
      <%
      if(sMetadataID == null){
      %>
         <mm:remove referid="metadata_id" />
         <mm:remove referid="target_node_id" />
         <mm:remove referid="meta_data_id" />
         <mm:createnode type="metadata" id="metadata_id"/>
         <mm:node number="<%= sNode %>" id="target_node_id">
            <mm:node number="<%= sMetadataDefinitionID %>" id="meta_data_id">
                <mm:createrelation source="metadata_id" destination="target_node_id" role="related" />
                <mm:createrelation source="metadata_id" destination="meta_data_id" role="related" />
            </mm:node>
         </mm:node>

         <mm:node referid="metadata_id">
            <mm:field name="number" jspvar="sID" vartype="String">
               <%
                sMetadataID = sID;
                %>
             </mm:field>
         </mm:node>
         <%
         if(bTemplatesActive){
            // Add default values to new metadata here
            String sTemplateNode = null;
            String sType = null;
            %>
            <mm:node number="<%= sMetadataDefinitionID %>">
               <mm:field name="type" jspvar="sMetadefType" vartype="String">
                   <%
                   sType = sMetadefType;
                   %>
               </mm:field>
               <mm:relatednodes type="metastandard" max="1" role="posrel">
                   <mm:related path="metadata,metadefinition">
                      <mm:field name="metadata.number" jspvar="sMetadataTemplateID" vartype="String">
                          <mm:field name="metadefinition.number" jspvar="sMetadefID" vartype="String">
                              <%
                              if(sMetadataDefinitionID.equals(sMetadefID)){
                                 sTemplateNode = sMetadataTemplateID;
                              }
                              %>
                          </mm:field>
                      </mm:field>
                   </mm:related>
                </mm:relatednodes>
            </mm:node>
            <%
            if (sTemplateNode != null){
            %>
               <mm:node number="<%= sTemplateNode %>">
                  <%
                  if(sType.equals("1")){
                     //vocabulary
                     %>
                     <mm:relatednodes type="metavocabulary" >
                        <mm:node id="vocabulary_id">
                           <mm:node number="<%= sMetadataID %>" id="metadata_id">
                              <mm:createrelation source="metadata_id" destination="vocabulary_id" role="posrel" />
                           </mm:node>
                        </mm:node>
                     </mm:relatednodes>
                     <%
                     } // end of if (sTemplateNode != null)

                    if(sType.equals("2")){
                      //date
                    %>
                    <mm:relatednodes type="metadate" max="1">
                       <mm:field name="value" jspvar="sDate" vartype="String" write="false">
                          <mm:remove referid="date_id" />
                          <mm:remove referid="metadata_id" />
                          <mm:createnode type="metadate" id="date_id">
                             <mm:setfield name="value"><%= sDate %></mm:setfield>
                          </mm:createnode>
                          <mm:node number="<%= sMetadataID %>" id="metadata_id">
                             <mm:createrelation source="metadata_id" destination="date_id" role="posrel" />
                          </mm:node>
                       </mm:field>
                    </mm:relatednodes>
                    <%
                    } // end of if(sType.equals("2"))

                    if(sType.equals("3")){
                       //lang sttrings
                       int iCounter = 1;
                       %>
                       <mm:related path="posrel,metalangstring" orderby="posrel.pos">
                          <mm:node element="metalangstring">
                             <mm:field name="language" jspvar="sLanguage" vartype="String" write="false">
                                <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                                   <mm:remove referid="lang_id" />
                                   <mm:remove referid="metadata_id" />
                                   <mm:createnode type="metalangstring" id="lang_id">
                                      <mm:setfield name="language"><%= sLanguage %></mm:setfield>
                                      <mm:setfield name="value"><%= sValue %></mm:setfield>
                                   </mm:createnode>
                                   <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                      <mm:createrelation source="metadata_id" destination="lang_id" role="posrel">
                                         <mm:setfield name="pos"><%= iCounter %></mm:setfield>
                                      </mm:createrelation>
                                   </mm:node>
                                </mm:field>
                             </mm:field>
                          </mm:node>
                          <%
                          iCounter++;
                          %>
                       </mm:related>
                    <%
                    } // end of if(sType.equals("3"))

                    if(sType.equals("4")){
                       //duration
                       int iCounter = 1;
                       %>
                       <mm:related path="posrel,metadate" max="2" orderby="posrel.pos">
                          <mm:node element="metadate">
                             <mm:field name="value" jspvar="sDate" vartype="String" write="false">
                                <mm:remove referid="date_id" />
                                <mm:remove referid="metadata_id" />
                                <mm:createnode type="metadate" id="date_id">
                                   <mm:setfield name="value"><%= sDate %></mm:setfield>
                                </mm:createnode>
                                <mm:node number="<%= sMetadataID %>" id="metadata_id">
                                   <mm:createrelation source="metadata_id" destination="date_id" role="posrel">
                                      <mm:setfield name="pos"><%= iCounter %></mm:setfield>
                                   </mm:createrelation>
                                </mm:node>
                             </mm:field>
                          </mm:node>
                          <%
                          iCounter++;
                          %>
                       </mm:related>
                    <%
                    } // end of if(sType.equals("4"))
                   %>
               </mm:node>
            <%
            } // end of if (sTemplateNode != null)
         } // end of if(bTemplatesActive)
      } // end of if (sMetadataID == null)
   session.setAttribute("metadata_id", sMetadataID);
   %>
</mm:cloud>
