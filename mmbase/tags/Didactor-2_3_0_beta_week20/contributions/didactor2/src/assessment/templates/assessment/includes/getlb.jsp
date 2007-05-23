<% 
   String backtolb = "-1"; 
   String listCompLo = "."; 
%>
<mm:node number="component.assessment" notfound="skip">
  <mm:relatednodes type="learnobjects" path="rolerel,learnobjects">
    <mm:field name="number" jspvar="dummy" vartype="String" write="false">
      <mm:first>
        <% backtolb = dummy; %>
      </mm:first>
      <% listCompLo += dummy + "."; %>
    </mm:field>
  </mm:relatednodes>
  <% if (!".".equals(listCompLo)) { %>
      <%@ include file="getlesson.jsp" %>
      <mm:node number="<%= lastClosedLesson %>" notfound="skip">
        <mm:tree type="learnobjects" role="posrel" searchdir="destination">
            <mm:field name="number" jspvar="dummy" vartype="String" write="false">
              <% if (listCompLo.indexOf("." + dummy + ".") != -1 ) { backtolb = dummy; } %>
            </mm:field>
        </mm:tree>
      </mm:node>
  <% } %>
</mm:node>
