<mm:functioncontainer>
  <mm:param name="format" value="mov,ram,wmp" />
  <mm:function name="format" jspvar="format" vartype="string">
    <% Format f = Format.get(format); %>
   <% if (f.isReal()) { %>
       <mm:write id="player" value="real" write="false" />
   <% } else if (f.isWindowsMedia()) { %>
       <mm:write id="player" value="qt" write="false" /> 
   <% } else { %>
       <mm:write id="player" value="qt" write="false" />
   <% } %>
  </mm:function>
</mm:functioncontainer>