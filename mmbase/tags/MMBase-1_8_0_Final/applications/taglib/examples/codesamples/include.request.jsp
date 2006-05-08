<p>
  <!--  Add one variable on request -->
  <mm:import id="a">Hoi</mm:import>
  <mm:write request="bla" referid="a"/>
  <!-- Another one, a bit more directly -->
  <mm:write request="bloe" value="Hi" />

  <!-- Also possible to associate a mm:context with the request, using the scope attribute -->
  <mm:context id="requestcontext" scope="request">
    <mm:import id="hoi">Hallo</mm:import>
    <mm:import id="hoi2">Hallo2</mm:import>
  </mm:context>

  <!-- include and use these vars -->
  <mm:include page="codesamples/included.request.jsp" />
</p>
