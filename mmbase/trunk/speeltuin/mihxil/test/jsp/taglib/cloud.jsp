<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud logout/login</h1>
  <p>
    Testing logout:
    <mm:cloud method="logout" />
  </p>
  <p>
    Testing method="http"
    <mm:cloud method="http" jspvar="cloud">
      You are logged in as: <%=cloud.getUser().getIdentifier() %>      
    </mm:cloud>
  </p>
  <p>
    This page does not work in MMBase 1.5 (will cause a loop).
  </p>
  <hr />
  <a href="cloud2.jsp">Next</a>    
   <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />

  </body>
</html>
