<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <h1>Cloud without attributes</h1>
    <p>
      <mm:cloud jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='asis' attribute</h1>
    <p>
      <mm:cloud method="asis" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must not be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='anonymous' attribute</h1>
    <p>
      <mm:cloud method="anonymous" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must be anonymous)
      </mm:cloud>
    </p>
    <h1>Cloud method='asis' attribute (again)</h1>
    <p>
      Anonymous cloud will not replace the one in the session.
      <mm:cloud method="asis" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must not be anonymous)
      </mm:cloud>
    </p>
    <hr />
    <a href="cloud2.jsp">Previous</a><br />
    <a href="cloud4.jsp">Next</a>
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
