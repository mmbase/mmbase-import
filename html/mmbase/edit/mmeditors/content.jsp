<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String nodeID = state.getProperty("node");
      String role = state.getProperty("role");
  %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <frameset cols="220,*" framespacing="0" frameborder="0" >
  <% if (nodeID == null) { %>
    <frame src="<mm:url page="select.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
  <% } else { %>
    <frame src="<mm:url page="editnode.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
  <% } %>  
    <frame src="<mm:url page="work.jsp" />" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
  </frameset>
</html>
