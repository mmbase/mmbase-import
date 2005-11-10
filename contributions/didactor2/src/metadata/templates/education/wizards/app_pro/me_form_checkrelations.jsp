<% // ??? for posrel.pos==3, assignedVals should not contain metadefinition %>
<mm:related  path="posrel,metadefinition" constraints="<%= "posrel.pos='" + CONSTRAINT_FORBIDDEN + "'" %>"  searchdir="source" fields="metadefinition.number" >
   <mm:field name="metadefinition.number" jspvar="rMd" vartype="String">
   <%
      bCheckRelations = ! hsetAssignedVals.contains(rMd);
   %>
   </mm:field>
</mm:related>
<%
if(bCheckRelations){
   // Let's check metavocabulary references ???
   // for posrel.pos==3, assignedVocabularis should not contain metavocabulary
   %>
   <mm:related path="posrel,metavocabulary" constraints="<%= "posrel.pos='" + CONSTRAINT_FORBIDDEN + "'" %>" searchdir="source" fields="metavocabulary.number" >
      <mm:field name="metavocabulary.number" jspvar="rMv" vartype="String">
         <%
         bCheckRelations = ! hsetVocabularis.contains(rMv);
         %>
      </mm:field>
   </mm:related>
<%
} // end of if(bCheckRelations)
%>
