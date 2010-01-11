<mm:functioncontainer>
  <mm:param name="format" value="ram,rm,asf,wmv,wmp,wmv,qt)" />
  <mm:function name="format" jspvar="format" vartype="string">
    <% Format f = Format.get(format); %>
   <% if (f == Format.MOV) { %>
       <mm:write id="player" value="qt" write="false" />
   <% } else if (f.isWindowsMedia()) { %>
       <mm:write id="player" value="wm" write="false" /> 
   <% } else { %>
       <mm:write id="player" value="real" write="false"/>
   <% } %>
  </mm:function>
</mm:functioncontainer>