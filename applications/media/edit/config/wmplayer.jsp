<mm:field name="format(wmp)" jspvar="url" vartype="string">
   <% if (url.equals("mov")) { %>
       <mm:write id="player" value="qt" />
   <% } else if (url.equals("ram")) { %>
       <mm:write id="player" value="real" /> 
   <% } else { %>
       <mm:write id="player" value="wm" />
   <% } %>
</mm:field>