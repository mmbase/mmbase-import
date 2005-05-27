<mm:import externid="todoname"/>
<mm:import externid="tododesc"/>
<mm:import externid="durationvalue"/>
<mm:import externid="durationmeasure"/>
<mm:import externid="todocomp">-1</mm:import>
<mm:import externid="todonumber">-1</mm:import>
<% msgString = "Persoonlijke taak is bijgewerkt"; %>
<mm:compare referid="todonumber" value="-1">
  <mm:maycreate type="todoitems">
    <% msgString = "Nieuwe persoonlijke taak is toegevoegd"; %>
    <mm:remove referid="todonumber"/>
    <mm:createnode type="todoitems" id="todonumber">
    </mm:createnode>
  </mm:maycreate>
</mm:compare>
<mm:node referid="todonumber">
  <mm:setfield name="name"><mm:write referid="todoname"/></mm:setfield>
  <mm:setfield name="description"><mm:write referid="tododesc"/></mm:setfield>
  <mm:setfield name="durationvalue"><mm:write referid="durationvalue"/></mm:setfield>
  <mm:setfield name="durationmeasure"><mm:write referid="durationmeasure"/></mm:setfield>
  <mm:related path="related,competencies">
    <mm:node element="related">
      <mm:deletenode deleterelations="true"/>
    </mm:node>
  </mm:related>
  <mm:related path="related,pop">
    <mm:node element="related">
      <mm:deletenode deleterelations="true"/>
    </mm:node>
  </mm:related>
</mm:node>
<mm:createrelation role="related" source="currentpop" destination="todonumber"/>
<mm:compare referid="currentcomp" value="-1" inverse="true">
  <mm:createrelation role="related" source="currentcomp" destination="todonumber"/>
</mm:compare>
<mm:compare referid="currentcomp" value="-1">
  <mm:compare referid="todocomp" value="-1" inverse="true">
    <mm:createrelation role="related" source="todocomp" destination="todonumber"/>
  </mm:compare>
</mm:compare>

