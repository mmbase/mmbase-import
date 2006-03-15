<%
Date date = null;
%>
<mm:relatednodes type="metadata" jspvar="mNode">
      <%
         if(nlRelatedNodes.contains(mNode))
         {
            %>
               <mm:relatednodes type="metadate" max="1">
                  <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                     <%
                        date = dateValue;
                     %>
                  </mm:field>
               </mm:relatednodes>
            <%
         }
      %>
</mm:relatednodes>
<table border="0" cellpadding="0" cellspacing="0" class="body">
   <tr>
      <td><di:translate key="metadata.day" /></td>
      <td><di:translate key="metadata.month" /></td>
      <td><di:translate key="metadata.year" /></td>
      <td>&nbsp;</td>
      <td><di:translate key="metadata.hour" /></td>
      <td><di:translate key="metadata.minute" /></td>
   </tr>
   <tr>
      <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(date.getDate()); %>" style="width:30px;"/></td>
      <td>
         <select name="m<%= sMetaDefinitionID %>">
             <option><%= MetaDataHelper.EMPTY_VALUE %></option>
             <%
                for(int i = 0; i < 12; i++)
                {
                   %>
                      <option value="<%= i + 1 %>"
                      <%
                         if((date!= null) && (date.getMonth() == i))
                         {
                            %> selected="selected" <%
                         }
                      %>
                      ><%= MetaDataHelper.MONTHS[i] %></option>
                   <%
                }
             %>
         </select>
      </td>
      <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date != null) out.print(1900 + date.getYear()); %>" style="width:60px;"/></td>
      <td>&nbsp;<di:translate key="metadata.at" />&nbsp;</td>
      <td>
         <select name="m<%= sMetaDefinitionID %>">
             <option><%= MetaDataHelper.EMPTY_VALUE %></option>
             <%
                for(int i = 0; i < 24; i++)
                {
                   %>
                      <option value="<%= i %>"
                      <%
                         if((date!= null) && (date.getHours() == i))
                         {
                            %> selected="selected" <%
                         }
                      %>
                      >
                      <%
                         if(i < 10)
                         {
                            %>0<%
                         }
                      %><%= i %></option>
                   <%
                }
             %>
         </select>
      </td>
      <td>
         <select name="m<%= sMetaDefinitionID %>">
             <option><%= MetaDataHelper.EMPTY_VALUE %></option>
             <%
                for(int i = 0; i < 60; i++)
                {
                   %>
                      <option value="<%= i %>"
                      <%
                         if((date!= null) && (date.getMinutes() == i))
                         {
                            %> selected="selected" <%
                         }
                      %>
                      >
                      <%
                         if(i < 10)
                         {
                            %>0<%
                         }
                      %><%= i %></option>
                   <%
                }
             %>
         </select>
      </td>
   </tr>
</table>
