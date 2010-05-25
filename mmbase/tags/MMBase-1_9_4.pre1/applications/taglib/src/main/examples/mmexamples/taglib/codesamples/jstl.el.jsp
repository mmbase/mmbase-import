<mm:import id="abc">5</mm:import>
<p>
  EL: <mm:write value="${6 + abc}" />, ${6 + abc}, <c:out value="${6 + abc}" />
</p>
<p>
  MMBase EL: <mm:write value="$[+ 6 + $abc]" />, <mm:write value="$[+ 6 +$abc]" vartype="integer" />
</p>

<mm:import id="varname">cba</mm:import>
<mm:import id="${varname}">kaboom</mm:import>
<p>
  <mm:write referid="${varname}" />,
  <mm:write value="$[$varname]" />,
  <c:out value="${cba}" />
</p>
