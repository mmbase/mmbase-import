<%@include file="/taglibs.jsp" %>
<html>
   <head>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
      <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
      <title>Simplestats</title>
   </head>
   <body>
      <h3>Page Counter</h3>
      <%= (Hashtable) application.getAttribute("pageCounter") %>
      <h3>Visitors Sessions</h3>
      <%= ((HashSet) application.getAttribute("visitorsSessions")).size() %>
      <h3>Visitors Counters</h3>
      <%= (Integer) application.getAttribute("visitorsCounter") %>
   </body>
</html>