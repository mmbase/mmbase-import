<mm:import externid="myfeedback1"/>
<mm:import externid="myfeedback2"/>
<% isEmpty = true; %>
<mm:compare referid="currentcomp" value="-1" inverse="true">
  <mm:list nodes="$currentpop" path="pop,popfeedback,people"
      constraints="people.number='$user'">
    <mm:node element="popfeedback">
      <mm:field name="number" jspvar="thisFeedback" vartype="String">
        <mm:list nodes="$currentcomp" path="competencies,popfeedback"
            constraints="<%= "popfeedback.number LIKE " + thisFeedback %>">
          <% isEmpty = false; %>
          <mm:remove referid="thisfeedback"/>
          <mm:remove referid="myfeedback1"/>
          <mm:remove referid="myfeedback2"/>
          <mm:import externid="thisfeedback"><mm:field name="popfeedback.number"/></mm:import>
          <mm:import externid="myfeedback1"><mm:field name="popfeedback.rank"/></mm:import>
          <mm:import externid="myfeedback2"><mm:field name="popfeedback.text"/></mm:import>
        </mm:list>
      </mm:field>
    </mm:node>
  </mm:list>
</mm:compare>