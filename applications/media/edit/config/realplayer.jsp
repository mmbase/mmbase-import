<mm:field name="format(ram)" jspvar="url" vartype="string">
   <% if (url.equals("mov")) { %>
       <mm:write id="player" value="qt" />
   <% } else if (url.equals("wmf")) { %>
       <mm:write id="player" value="wm" /> 
   <% } else { %>
       <mm:write id="player" value="real" />
   <% } %>
</mm:field>