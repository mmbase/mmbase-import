<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud logout/loginpage</h1>
    <p>
      First logging out. <mm:cloud method="logout" />
    </p>
    <p>
      Testing loginpage:
      <mm:cloud loginpage="login.jsp" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>      
      </mm:cloud>
    </p>
    <p>
      This page does not work in MMBase 1.5 (loginpage attribute not supported).
    </p>
    <hr />
    <a href="cloud1.jsp">Previous (interesting if you logged in as
    different user now)</a><br />
    <a href="cloud3.jsp">Next</a>
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
