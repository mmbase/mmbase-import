<%@include file="../header.jsp" %>
  <mm:cloud method="http" jspvar="cloud">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String nodeID = state.getProperty("node");
      String managerName = state.getProperty("manager");
  %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="../css/mmeditors.css" type="text/css" />
    <style>
<%@include file="../css/mmeditors.css" %>     
    </style>
  </head>
    <mm:import externid="field"/>
    <mm:present referid="field">
      <mm:transaction name="<%=transactionID%>" commitonclose="false">
        <mm:node number="<%=nodeID%>">
          <mm:field name="$field">
            <mm:fieldinfo type="useinput" />
            <% state.put("state","changed");%>
          </mm:field>
        </mm:node>
      </mm:transaction>
    </mm:present>
<%@include file="nextfield.jsp" %> 
  </mm:cloud>
</html>

