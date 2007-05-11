<c:set var="x" value="xxxxx"/>
<p>
  <mm:write referid="x" />, <mm:write value="${x}" />, <mm:write value="$x" />, <c:out value="${x}"/>
</p>
<mm:import id="y">yyyyy</mm:import>
<p>
  <mm:write referid="y" />, <mm:write value="${y}" />, <mm:write value="$y" />, <c:out value="${y}"/>
</p>
