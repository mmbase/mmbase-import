<%
int iCounter = 0;
boolean bEmpty = true;
%>
<mm:relatednodes type="metadata" jspvar="mNode">
      <%
         if(nlRelatedNodes.contains(mNode))
         {
            %>
               <mm:related path="posrel,metalangstring" orderby="posrel.pos">
                  <mm:node element="metalangstring">
                     <mm:field name="language" jspvar="sLanguage" vartype="String" write="false">
                        <mm:field name="value" jspvar="sValue" vartype="String" write="false">
                           <mm:field name="number" jspvar="sMetalangID" vartype="String" write="false">
                              <table border="0" cellpadding="0" cellspacing="0">
                                 <tr>
                                    <td>
                                       <select name="m<%= sMetaDefinitionID %>">
                                          <option style="width:20px"><%= mdh.EMPTY_VALUE %></option>
                                             <%
                                                for(NodeIterator it = nlLangCodes.nodeIterator(); it.hasNext();)
                                                {
                                                   Node nLangCode = (Node) it.next();
                                                   String sLangCode = nLangCode.getStringValue("metavocabulary.value");
                                                   %>
                                                      <option value="<%= sLangCode %>"
                                                         <%
                                                            if(sLanguage.equals(sLangCode))
                                                            {
                                                               %>selected="selected"<%
                                                            }
                                                         %>
                                                      ><%= sLangCode %></option>
                                                   <%
                                                }
                                                bEmpty = false;
                                             %>
                                       </select>
                                    </td>
                                    <td>
                                       <input name="m<%= sMetaDefinitionID %>" type="text" value="<%= sValue %>" style="width:150px"/>
                                    </td>
                                    <td>
                                       &nbsp;
                                    </td>
                                    <td>
                                       <input type="image" src="gfx/minus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='remove'; add.value='<%= sMetaDefinitionID %>,<%= iCounter %>'">
                                    </td>
                                 </tr>
                              </table>
                           </mm:field>
                        </mm:field>
                     </mm:field>
                  </mm:node>
                  <%
                     iCounter++;
                  %>
               </mm:related>
            <%
         }
      %>
</mm:relatednodes>
<%
   if(bEmpty)
   {
      %>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td>
                  <select name="m<%= sMetaDefinitionID %>">
                     <option style="width:20px"><%= mdh.EMPTY_VALUE %></option>
                        <%
                           boolean bFirst = true;
                           for(NodeIterator it = nlLangCodes.nodeIterator(); it.hasNext();)
                           {
                              Node nLangCode = (Node) it.next();
                              String sLangCode = nLangCode.getStringValue("metavocabulary.value");
                              %>
                                 <option value="<%= sLangCode %>"
                                    <%
                                       if(bFirst)
                                       {
                                          %>selected="selected"<%
                                          bFirst = false;
                                       }
                                    %>
                                 ><%= sLangCode %></option>
                              <%
                           }
                           iCounter++;
                           bEmpty = false;
                        %>
                  </select>
               </td>
               <td>
                  <input name="m<%= sMetaDefinitionID %>" type="text" value="" style="width:150px"/>
               </td>
            </tr>
         </table>

      <%
   }
%>
<input type="image" src="gfx/plus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='add'; add.value='<%= sMetaDefinitionID %>'"> <di:translate key="metadata.add_more_langstrings" />
