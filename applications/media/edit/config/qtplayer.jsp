<mm:field name="format(mov)" jspvar="url" vartype="string">
   <% if (url.equals("ram")) { %>
       <mm:write id="player" value="real" />
   <% } else if (url.equals("wmf")) { %>
       <mm:write id="player" value="wm" /> 
   <% } else { %>
       <mm:write id="player" value="qt" />
   <% } %>
</mm:field>