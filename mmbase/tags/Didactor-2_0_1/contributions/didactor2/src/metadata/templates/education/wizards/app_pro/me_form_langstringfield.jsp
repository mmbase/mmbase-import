<%
int iCounter = 0;
boolean bEmpty = true;
%>
<mm:relatednodes type="metadata">
   <mm:field name="number" jspvar="sID" vartype="String">
      <%
         if(hsetRelatedNodes.contains(sID))
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
                                       <select name="<%=sPrefix%><%= sMetaDefinitionID %>">
                                          <option style="width:20px"><%= EMPTY_VALUE %></option>
                                             <%
                                                for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                                                {
                                                   String sLangCode = (String) it.next();
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
                                       <input name="<%=sPrefix%><%= sMetaDefinitionID %>" type="text" value="<%= sValue %>" style="width:150px"/>
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
   </mm:field>
</mm:relatednodes>
<%
   if(bEmpty)
   {
      %>
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <td>
                  <select name="<%=sPrefix%><%= sMetaDefinitionID %>">
                     <option style="width:20px"><%= EMPTY_VALUE %></option>
                        <%
                           boolean bFirst = true;
                           for(Iterator it = hsetLangCodes.iterator(); it.hasNext();)
                           {
                              String sLangCode = (String) it.next();
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
                  <input name="<%=sPrefix%><%= sMetaDefinitionID %>" type="text" value="" style="width:150px"/>
               </td>
            </tr>
         </table>

      <%
   }
%>
<input type="image" src="gfx/plus.gif" onClick="meta_form.action='#m<%= sMetaDefinitionID %>'; submitted.value='add'; add.value='<%= sMetaDefinitionID %>'"> voeg meer tekenreeksen toe
