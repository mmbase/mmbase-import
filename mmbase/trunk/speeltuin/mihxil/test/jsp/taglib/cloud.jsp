<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud logout/login</h1>
    <mm:cloud method="logout" />
    <mm:cloud method="http" jspvar="cloud">
      You are logged in as: <%=cloud.getUser().getIdentifier() %>      
    </mm:cloud>
    
  </body>
</html>
