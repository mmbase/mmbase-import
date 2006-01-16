<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
  <head>

  </head>
  <body>
    <h1>Cloud logout/login</h1>
    <mm:log>logout</mm:log>
  <p>
    Testing logout <mm:cloud method="logout" />.
  </p>
    <mm:log>login</mm:log>
  <p>
    Testing method="http"
    <mm:cloud method="http" jspvar="cloud">
      You are <mm:cloudinfo type="user" /> (<mm:cloudinfo type="rank" />)
      <mm:hasrank value="administrator">
        You are administrator !!
      </mm:hasrank>
      <mm:hasrank minvalue="administrator" inverse="true">
        You are not administrator!
      </mm:hasrank> 
        Cloud: <mm:cloudinfo type="name" />
      </p>
    </mm:cloud>
  </p>
  <p>
    Do a shift-reload on this page. Again a login-box must popup
    (because logout was done). Try also what happens if you press
    'cancel' of try a wrong password. Results must be sensible.
  </p>
  <p>
    This page does not work in MMBase 1.5 (will cause a loop).
  </p>
  <hr />
  <a href="<mm:link page="cloud1.jsp" />">Next</a><br />
   <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />

  </body>
</html>
