<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
  <head>
  </head>
  <body>
    <h1>Cloud logout/loginpage</h1>
    <p>
      First logging out. <mm:cloud method="logout" />
    </p>
    <p>
      Testing loginpage:
      <mm:cloud loginpage="login.jsp">
        You are logged in as: <mm:cloudinfo type="user" />
      </mm:cloud>
    </p>
    <p>
      This page does not work in MMBase 1.5 (loginpage attribute not supported).
    </p>
    <hr />
    <a href="<mm:url page="cloud1.jsp" />">Previous (interesting if you logged in as different user now)</a><br />
    <a href="<mm:url page="cloud3.jsp" />">Next</a>
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
