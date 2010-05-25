<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%>
<mm:import from="request" externid="bla" />
<mm:import from="request" externid="bloe" />
<p>
  <mm:write referid="bla" /> (Should be 'Hoi')
  <br />
  <mm:write referid="bloe" /> (Should be 'Hi')
</p>
<p>
<mm:context id="requestcontext" scope="request">
  <mm:write referid="hoi" /> (Should  be 'Hallo')
  <br />
  <mm:write referid="hoi2" /> (Should be 'Hallo2')
</mm:context>
</p>