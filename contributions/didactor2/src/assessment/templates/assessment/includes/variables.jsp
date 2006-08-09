<%
   String[] problemWeights = { "0", "1", "very much", "3", "much", "5", "much/little", "7", "little", "9", "nothing" };
   // todo - from di:translate
   String thisUser = "-1";
%>
<mm:node number="$user" notfound="skip">
  <mm:field name="number" jspvar="dummy" vartype="String" write="false">
    <% thisUser = dummy; %>
  </mm:field>
</mm:node>