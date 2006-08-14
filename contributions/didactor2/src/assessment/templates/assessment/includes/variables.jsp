<%
   String[] problemWeights = { "0", "1", "nothing", "3", "little", "5", "much/little", "7", "much", "9", "very much" };
   // todo - from di:translate
   String thisUser = "-1";
%>
<mm:node number="$user" notfound="skip">
  <mm:field name="number" jspvar="dummy" vartype="String" write="false">
    <% thisUser = dummy; %>
  </mm:field>
</mm:node>