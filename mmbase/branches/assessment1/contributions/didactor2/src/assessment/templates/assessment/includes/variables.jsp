<%
   String[] problemWeights = { "0", "1", "nothing", "3", "little", "5", "much_little", "7", "much", "9", "very_much" };
   for (int i=2; i<=10; i+=2) {
%> 
     <mm:import id="weight" jspvar="weight" vartype="String" reset="true"><di:translate key="<%= "assessment." + problemWeights[i] %>" /></mm:import>
<%
     problemWeights[i] = weight;
   }
   String thisUser = "-1";
%>
<mm:remove referid="weight"/>
<mm:node number="$user" notfound="skip">
  <mm:field name="number" jspvar="dummy" vartype="String" write="false">
    <% thisUser = dummy; %>
  </mm:field>
</mm:node>
