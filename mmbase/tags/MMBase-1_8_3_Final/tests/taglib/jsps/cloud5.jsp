<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <mm:import externid="from" from="parameters">this page</mm:import>
    <h1>Cloud method='asis' attribute then loginpage.</h1>
    <p>
      Logged out in previous-page, so 'asis' must be anonymous:
      <mm:cloud method="asis">
        You are logged in as: <mm:cloudinfo type="user" />
        (must be anonymous)
      </mm:cloud>
    </p>
    <mm:log>xxx</mm:log>
    <p>
      <mm:cloud  loginpage="login.jsp?referrer=cloud5.jsp">
        You are logged in as: <mm:cloudinfo type="user" />
        (must not be anonymous)
      </mm:cloud>
    </p>
    <p>
     From: <mm:write referid="from" /> (must be '4' if you clicked from cloud4.jsp)
    </p>
  <hr />
  <a href="<mm:url page="cloud4.jsp" />">Previous</a><br />
  <a href="<mm:url page="cloud6.jsp" />">Next</a><br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
