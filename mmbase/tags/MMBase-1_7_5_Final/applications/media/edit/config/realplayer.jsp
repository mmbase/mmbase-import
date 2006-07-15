<mm:field name="format(ram,wmp,qt)" jspvar="url" vartype="string">
   <% if (url.equals("mov")) { %>
       <mm:write id="player" value="qt" write="false" />
   <% } else if (url.equals("wmp")) { %>
       <mm:write id="player" value="wm" write="false" /> 
   <% } else if (url.equals("asf")) { %>
       <mm:write id="player" value="wm" write="false" /> 
   <% } else { %>
       <mm:write id="player" value="real" write="false"/>
   <% } %>
</mm:field>