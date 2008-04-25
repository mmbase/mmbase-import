<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <mm:import externid="manager" vartype="String" jspvar="managerName" />
  <mm:import externid="node" vartype="String" jspvar="nodealias" />
  <mm:import externid="role" vartype="String" jspvar="role" />
  <mm:import externid="action" />
  <mm:import externid="createrelation" />
  <mm:import externid="depth" vartype="Integer" jspvar="depth" >0</mm:import>
  <%  Stack states = (Stack)session.getValue("mmeditors_states"); %>
  <mm:present referid="manager">
    <% // clean states stack to prevent corruption through reloads of a pages
       while (depth.intValue() < states.size()) {
         Properties dropstate= (Properties)states.pop();
         String transactionID = dropstate.getProperty("transaction");
         if (transactionID!=null) try {
           Transaction trans=cloud.getTransaction(transactionID);
           trans.cancel();
         } catch (Exception e) {}
       }
       // create a new state if manager is given
       // skip if manager is '?' since this indicates a back action
       if (!managerName.equals("?")) {
           Properties newstate = new Properties();
           newstate.put("manager", managerName);
           newstate.put("state","none");
           states.push(newstate);
       }
    %>
  </mm:present>
<%  Properties state= (Properties)states.peek(); %>
  <mm:present referid="node">
      <% state.put("node",nodealias); %>
  </mm:present>
  <mm:present referid="role">
      <% state.put("role",role); %>
  </mm:present>
<%
  String currentState = state.getProperty("state");
  String transactionID = state.getProperty("transaction");
  role = state.getProperty("role");
  String nodeID = state.getProperty("node");
  Transaction trans = null;
  
%>  
  <mm:present referid="action">
    <%
       // should have transaction
       trans = cloud.getTransaction(transactionID);
       Node node = trans.getNode(nodeID);
       boolean isRelation = node.isRelation();
    %>
    <mm:compare referid="action" value="deletenode">
    <%
       node.delete();
       trans.commit();
       nodeID = null;
       state.remove("node");
    %>
    </mm:compare>
    <mm:compare referid="action" value="save">
    <% 
       trans.commit();
       // place number of node after commit
       nodeID = ""+node.getNumber();
       state.put("node",nodeID);
    %>
    </mm:compare>
    <mm:compare referid="action" value="cancel">
    <% trans.cancel();
       if ("new".equals(currentState)) {
           nodeID = null;
           state.remove("node");
       }
    %>
    </mm:compare>
    <% state.remove("transaction");
       currentState = "none";
       state.put("state",currentState);
       // when committing or cancelling the editing/deletion/creation of a relation, 
       // remove the state - otherwise we get a select!
       if (isRelation) {
           states.pop();
       }
    %>
  </mm:present>

  <mm:present referid="createrelation">
  <%
    if (nodeID != null && role != null && "none".equals(currentState)) {
        // pop the first state ( holds the destination object)
        Properties destination = (Properties)states.pop();
        // then peek the second state ( holds the source object)
        Properties source = (Properties)states.peek();

        trans = cloud.createTransaction();
        transactionID = trans.getName();
        RelationManager manager = trans.getRelationManager(role);
        managerName = manager.getName();

        String nodeIDsource = destination.getProperty("node");
        Node nodeSource = trans.getNode(nodeIDsource);
        String nodeIDdestination = source.getProperty("node");
        Node nodeDestination = trans.getNode(nodeIDdestination);
       
        Node relation = manager.createRelation(nodeSource, nodeDestination);

        if (!manager.getName().equals("insrel")) {
          state = new Properties();
          state.put("state","none");
          state.put("transaction",transactionID);
          state.put("role",role);
          state.put("manager", managerName);
          state.put("node",""+relation.getNumber());
          state.put("state","new");
          states.push(state);
        } else {
          trans.commit();
        }
    }
  %>
</mm:present>
  <head>
    <title>Editor</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <frameset rows="23,*" border="0" frameborder="0" framespacing="0">
    <frame src="<mm:url page="editnav.jsp" />" name="navigatorarea" scrolling="no" marginheight="0" marginwidth="0" />
    <frame src="<mm:url page="content.jsp" />" name="contentarea" scrolling="no" marginheight="0" marginwidth="0" />
  </frameset>
  </mm:cloud>
</html>
