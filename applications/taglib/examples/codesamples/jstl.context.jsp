<h2>named context</h2>
<mm:context id="test">
  <mm:import id="abc">AAA</mm:import>
  <p>
    <mm:write referid="abc" />,      
    <mm:write value="$abc" />,  <mm:write value="${abc}" />, <mm:write value="$[abc]" />,
    <c:out value="${abc}" />, ${abc},

    <mm:write referid="test.abc" />, 
    <mm:write  value="$test.abc" />, <mm:write value="${test.abc}" />, <mm:write value="$[test.abc]" />
    <c:out value="${test.abc}" />, ${test.abc}
  </p>
</mm:context>
<p>
  Outside context you must prefix with name of context.
</p>
<p>
  <mm:write referid="test.abc" />, 
  <mm:write value="$test.abc" />, <mm:write value="${test.abc}" />, <mm:write value="$[test.abc]" />,
  <c:out value="${test.abc}" />, ${test.abc}
</p>

<h2>anonymous context</h2>
<mm:import id="abd">BBB</mm:import>
<mm:context>
  <mm:import id="abd">CCC</mm:import>
  <p>
    <mm:write referid="abd" />,      
    <mm:write value="$abd" />,  <mm:write value="${abd}" />, <mm:write value="$[abd]" />,
    <c:out value="${abd}" />, ${abd}
  </p>
</mm:context>
<p>
  Should be BBB's.
  <mm:write referid="abd" />,      
  <mm:write value="$abd" />,  <mm:write value="${abd}" />, <mm:write value="$[abd]" />,
  <c:out value="${abd}" />, ${abd}
</p>
<h2>listbody context</h2>
<mm:import id="list" vartype="list">A,B</mm:import>
<mm:stringlist referid="list">
  <mm:import id="abe">DDD</mm:import>
  <mm:first>
    <p>
      <mm:write referid="abe" />,      
      <mm:write value="$abe" />,  <mm:write value="${abe}" />, <mm:write value="$[abe]" />,
      <c:out value="${abe}" />, ${abe}
    </p>
  </mm:first>
</mm:stringlist>
<p>
  listbody's are no real contexts (and not at all in 1.7), so the variable inside the list must be
  available outside it.
</p>
<p>
  <mm:write referid="abe" />,      
  <mm:write value="$abe" />,  <mm:write value="${abe}" />, <mm:write value="$[abe]" />,
  <c:out value="${abe}" />, ${abe}
</p>
