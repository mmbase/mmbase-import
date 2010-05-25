<mm:functioncontainer>
  <mm:param name="format" value="asf,wmv,wmp,wmv,ram,rm,qt)" />
  <mm:function name="format" jspvar="format" vartype="string">
    <% Format f = Format.get(format); %>
   <% if (f == Format.MOV) { %>
       <mm:write id="player" value="qt" write="false" />
   <% } else if (f.isReal()) { %>
       <mm:write id="player" value="real" write="false"/> 
   <% } else { %>
       <mm:write id="player" value="wm" write="false" />
   <% } %>
  </mm:function>
</mm:functioncontainer>