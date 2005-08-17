<%
Date date = null;
%>
<mm:relatednodes type="metadata">
 <mm:field name="number" jspvar="sID" vartype="String">
     <%
        if(hsetRelatedNodes.contains(sID))
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
  </mm:field>
</mm:relatednodes>

<table border="0" cellpadding="0" cellspacing="0" class="body">
   <tr>
      <td>Dag</td>
      <td>Maand</td>
      <td>Jaar</td>
      <td>&nbsp;</td>
      <td>Uur</td>
      <td>Minuut</td>
   </tr>
   <tr>
      <td><input type="text" name="<%=sPrefix%><%= sMetaDefinitionID %>" value="<% if (date != null) out.print(date.getDate()); %>" style="width:30px;"/></td>
      <td>
         <select name="<%=sPrefix%><%= sMetaDefinitionID %>">
             <option><%= EMPTY_VALUE %></option>
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
                      ><%= MONTHS[i] %></option>
                   <%
                }
             %>
         </select>
      </td>
      <td><input type="text" name="<%=sPrefix%><%= sMetaDefinitionID %>" value="<% if (date != null) out.print(1900 + date.getYear()); %>" style="width:60px;"/></td>
      <td>&nbsp;om&nbsp;</td>
      <td>
         <select name="<%=sPrefix%><%= sMetaDefinitionID %>">
             <option><%= EMPTY_VALUE %></option>
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
         <select name="<%=sPrefix%><%= sMetaDefinitionID %>">
             <option><%= EMPTY_VALUE %></option>
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
