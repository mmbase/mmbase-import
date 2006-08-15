<mm:import id="assessment_education">-1</mm:import>
<mm:node number="component.assessment" id="component_assessment"/>
<mm:node number="$provider" notfound="skip">
  <mm:related path="related,educations,settingrel,components" constraints="components.number=$component_assessment">
    <mm:import id="assessment_education" reset="true"><mm:field name="educations.number"/></mm:import>
  </mm:related>
</mm:node>