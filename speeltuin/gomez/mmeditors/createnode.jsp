<%@include file="header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String currentState = state.getProperty("state");
      // close current transaction
      if (transactionID != null) { 
          Transaction oldtrans = cloud.getTransaction(transactionID);
          oldtrans.cancel();
      }
      Transaction trans = cloud.createTransaction();
      transactionID = trans.getName();
      state.put("transaction",transactionID);
      String managerName = state.getProperty("manager");
      NodeManager manager = trans.getNodeManager(managerName);
      Node node = manager.createNode();
      state.put("node",""+node.getNumber());
      state.put("state","new");
    %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>     
    </style>
  </head>
  <frameset cols="220,*" framespacing="0">
    <frame src="<mm:url page="editnode.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
    <frame src="<mm:url page="work.jsp" />" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
  </frameset>
  </mm:cloud>
</html>
