
<mm:context id="test">
  <mm:import id="abc">HFF</mm:import>
  <p>
    <mm:write value="$abc" />, <mm:write value="${test.abc}" />, <c:out value="${test.abc}" />,
  </p>
</mm:context>
<p>
  <mm:write referid="test.abc" />, <mm:write value="$test.abc" />, <c:out value="${test.abc}" />
</p>
