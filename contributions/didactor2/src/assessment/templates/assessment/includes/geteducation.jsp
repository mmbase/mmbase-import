<mm:import id="assessment_education">-1</mm:import>
<mm:node number="component.assessment" id="component_assessment"/>
<mm:node number="$provider" notfound="skip">
  <mm:related path="related,educations,settingrel,components" constraints="components.number=$component_assessment">
    <mm:import id="assessment_education" reset="true"><mm:field name="educations.number"/></mm:import>
  </mm:related>
</mm:node>

<mm:import id="assessment_evaluationtest">-1</mm:import>
<mm:node number="$provider" notfound="skip">
  <mm:related path="related,educations,related,tests">
    <mm:node element="tests">
      <mm:import id="isnotproblemtyperelated" reset="true" />
      <mm:related path="related,problemtypes">
        <mm:remove referid="isnotproblemtyperelated" />
      </mm:related>
      <mm:present referid="isnotproblemtyperelated">
        <mm:import id="assessment_evaluationtest" reset="true"><mm:field name="number"/></mm:import>
      </mm:present>
    </mm:node>
  </mm:related>
</mm:node>
