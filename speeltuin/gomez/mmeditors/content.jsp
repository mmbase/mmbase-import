<%@include file="header.jsp" %>
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String nodeID = state.getProperty("node");
      String role = state.getProperty("role");
  %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>     
    </style>
  </head>
  <frameset cols="220,*" framespacing="0">
  <% if (nodeID == null) { %>
    <frame src="<mm:url page="select.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
  <% } else { %>
    <frame src="<mm:url page="editnode.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
  <% } %>  
    <frame src="<mm:url page="work.jsp" />" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
  </frameset>
</html>
