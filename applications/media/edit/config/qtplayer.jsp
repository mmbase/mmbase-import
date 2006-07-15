<mm:field name="format(mov,ram,wmp)" jspvar="url" vartype="string">
   <% if (url.equals("ram")) { %>
       <mm:write id="player" value="real" write="false" />
   <% } else if (url.equals("wmp")) { %>
       <mm:write id="player" value="qt" write="false" /> 
   <% } else { %>
       <mm:write id="player" value="qt" write="false" />
   <% } %>
</mm:field>