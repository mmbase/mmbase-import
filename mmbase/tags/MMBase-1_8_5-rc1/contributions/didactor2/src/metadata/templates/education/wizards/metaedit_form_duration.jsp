<%
Date[] date = new Date[2];
%>
<mm:relatednodes type="metadata" jspvar="mNode">
<%
   if(nlRelatedNodes.contains(mNode))
   {
      %>
         <mm:related path="posrel,metadate" orderby="posrel.pos">
            <mm:node element="metadate">
               <mm:first>
                  <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                     <%
                        date[0] = dateValue;
                     %>
                  </mm:field>
               </mm:first>
               <mm:first inverse="true">
                  <mm:field name="value" jspvar="dateValue" vartype="Date" write="false">
                     <%
                        date[1] = dateValue;
                     %>
                  </mm:field>
               </mm:first>
            </mm:node>
         </mm:related>
      <%
   }
%>
</mm:relatednodes>
<%
for(int f = 0; f < 2; f++)
{
   %>
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
         <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(date[f].getDate()); %>" style="width:30px;"/></td>
         <td>
            <select name="m<%= sMetaDefinitionID %>">
                <option><%= MetaDataHelper.EMPTY_VALUE %></option>
                <%
                   for(int i = 0; i < 12; i++)
                   {
                      %>
                         <option value="<%= i + 1 %>"
                         <%
                            if((date[f]!= null) && (date[f].getMonth() == i))
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
         <td><input type="text" name="m<%= sMetaDefinitionID %>" value="<% if (date[f] != null) out.print(1900 + date[f].getYear()); %>" style="width:60px;"/></td>
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
                            if((date[f]!= null) && (date[f].getHours() == i))
                            {
                               %> selected="selected" <%
                            }
                         %>
                         >
                         <%= ( i<10 ? "0" : "") + i %></option>
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
                            if((date[f]!= null) && (date[f].getMinutes() == i))
                            {
                               %> selected="selected" <%
                            }
                         %>
                         >
                         <%= ( i<10 ? "0" : "") + i %></option>
                      <%
                   }
                %>
            </select>
         </td>
      </tr>
   </table>
   <%
}
%>
