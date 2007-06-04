<% editcontextID = "ec_" + editcontextName.replaceAll("\\s+","_"); %>
<% constraints = "editcontexts.name='" + editcontextName + "'"; %>
<mm:import id="<%= editcontextID %>">-1</mm:import>
<mm:list path="editcontexts" constraints="<%= constraints %>">
   <mm:import id="<%= editcontextID %>" reset="true"><mm:field name="editcontexts.number"/></mm:import>
</mm:list>

<mm:compare referid="<%= editcontextID %>" value="-1">
   Editcontext "<%= editcontextName %>" not found. Creating...<br/>
   <mm:remove referid="<%= editcontextID %>"/>
   <mm:createnode type="editcontexts" id="<%= editcontextID %>">
      <mm:setfield name="name"><%= editcontextName %></mm:setfield>
   </mm:createnode>
</mm:compare>
<mm:write referid="<%= editcontextID %>" jspvar="dummy" vartype="String" write="false">
   <% allEditcontexts += dummy + ","; %>
</mm:write>
