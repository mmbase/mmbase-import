<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
  <head>
  </head>
  <body>
    <h1>Cloud method='http', already logged in</h1>
    <p>
      <mm:cloud method="http" jspvar="cloud">
        You are logged in as: <%=cloud.getUser().getIdentifier() %>
        (must not be anonymous, no login-box should have appeared)
      </mm:cloud>
    </p>
    <hr />
    <a href="<mm:url page="cloud.jsp" />">Previous</a><br />
    <a href="<mm:url page="cloud2.jsp" />">Next</a><br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
