<% 
   String backtolb = "-1"; 
   String listCompLo = "."; 
%>
<mm:node number="component.assessment" notfound="skip">
  <mm:relatednodes type="learnobjects" path="rolerel,learnobjects">
    <mm:field name="number" jspvar="dummy" vartype="String" write="false">
      <% listCompLo += dummy + ".";
      %>
    </mm:field>
  </mm:relatednodes>
  <% if (!".".equals(listCompLo)) { %>
      <%@ include file="getlesson.jsp" %>
      <mm:node number="<%= currentLesson %>" notfound="skip">
        <mm:tree type="learnobjects" role="posrel" searchdir="destination">
            <mm:field name="number" jspvar="dummy" vartype="String" write="false">
              <% if (listCompLo.indexOf("." + dummy + ".") != -1 ) { backtolb = dummy; } %>
            </mm:field>
        </mm:tree>
      </mm:node>
  <% } %>
</mm:node>