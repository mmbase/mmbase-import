<mm:field name="format(wmp)" jspvar="url" vartype="string">
   <% if (url.equals("mov")) { %>
       <mm:write id="player" value="qt" write="false" />
   <% } else if (url.equals("ram")) { %>
       <mm:write id="player" value="real" write="false"/> 
   <% } else { %>
       <mm:write id="player" value="wm" write="false" />
   <% } %>
</mm:field>