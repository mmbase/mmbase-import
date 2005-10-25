<%	TreeMap allCompetencies = new TreeMap();
// 1 - pop-havecomp-competencies
// 2 - pop-developcomp-competencies
// 3 - pop-needcomp-competencies
// 4 - profiles-related-competencies
%>
<mm:import externid="currentprofile" jspvar="currentProfile" reset="true">-1</mm:import>
<% String profileConstraint = ""; %>
<mm:compare referid="currentprofile" value="-1" inverse="true">
<% profileConstraint = "profiles.number='" + currentProfile + "'"; %>
</mm:compare>
<mm:list nodes="$currentpop" path="pop,related,profiles,related,competencies" orderby="competencies.number" directions="UP" 
    constraints="<%= profileConstraint %>">
  <mm:field name="competencies.number" jspvar="thisCompetencie" vartype="String">
    <% allCompetencies.put(thisCompetencie, new Integer(4)); %>
  </mm:field>
</mm:list>
<mm:list nodes="$currentpop" path="pop,needcomp,competencies" orderby="competencies.number" directions="UP">
  <mm:field name="competencies.number" jspvar="thisCompetencie" vartype="String">
    <% allCompetencies.put(thisCompetencie, new Integer(3)); %>
  </mm:field>
</mm:list>
<mm:list nodes="$currentpop" path="pop,havecomp,competencies" orderby="competencies.number" directions="UP">
  <mm:field name="competencies.number" jspvar="thisCompetencie" vartype="String">
    <% allCompetencies.put(thisCompetencie, new Integer(1)); %>
  </mm:field>
</mm:list>
<mm:list nodes="$currentpop" path="pop,developcomp,competencies" orderby="competencies.number" directions="UP">
  <mm:field name="competencies.number" jspvar="thisCompetencie" vartype="String">
    <% allCompetencies.put(thisCompetencie, new Integer(2)); %>
  </mm:field>
</mm:list>
