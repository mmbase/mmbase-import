<mm:import id="hoi">Hoi</mm:import>
<p>
  <mm:include referids="hoi" page="codesamples/included.jsp" /> (Must show 'Hoi')
</p>
<p>
  <mm:include page="codesamples/included.jsp" />  (Must show UNSET, or 'Tot Ziens')
  <br />
  <mm:include page="codesamples/included.jsp"><mm:param name="hoi" value="$hoi" /></mm:include> (Must show 'Hoi')
</p>
<p>
  <jsp:include page="codesamples/included.jsp" /> (Must show UNSET, or 'Tot Ziens')
  <br />
  <jsp:include page="codesamples/included.jsp"><jsp:param name="hoi" value="Hallo" /></jsp:include> (Must show 'Hallo')
</p>
