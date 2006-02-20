<mm:import externid="myfeedback1"/>
<mm:import externid="myfeedback2"/>
<mm:import id="thisfeedback" reset="true">-1</mm:import>
<% isEmpty = true; %>
<mm:compare referid="currentcomp" value="-1" inverse="true">
  <mm:list nodes="$currentpop" path="pop,popfeedback,people"
      constraints="people.number='$student'">
    <mm:node element="popfeedback">
      <mm:field name="number" jspvar="thisFeedback" vartype="String">
        <mm:list nodes="$currentcomp" path="competencies,popfeedback"
            constraints="<%= "popfeedback.number='" + thisFeedback + "'" %>">
          <% isEmpty = false; %>
          <mm:import id="thisfeedback" reset="true"><mm:field name="popfeedback.number"/></mm:import>
          <mm:import externid="myfeedback1" reset="true"><mm:field name="popfeedback.rank"/></mm:import>
          <mm:import externid="myfeedback2" reset="true"><mm:field name="popfeedback.text"/></mm:import>
        </mm:list>
      </mm:field>
    </mm:node>
  </mm:list>
</mm:compare>
