<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
  <head>

  </head>
 
    <h1>Cloud rank="administrator", no method, method must be request from bridge.</h1>
    <p>
      <mm:cloud rank="administrator">
        Your rank: <mm:cloudinfo type="user" />/<mm:cloudinfo type="rank" />
        (must be administrator).
        Should see this.
         <mm:import id="body" />
      </mm:cloud>
      <mm:notpresent referid="body">
        Skipped body. Should not see this!
      </mm:notpresent>  
    </p>
    <mm:log>---------------------------------------------------------------------------------------</mm:log>
    <h1>Cloud rank="administrator", method 'asis', but there is not cloud because  first logged out</h1>
    <p>
      <mm:cloud method="logout" />
      <mm:cloud rank="administrator" jspvar="cloud" method="asis">
        Your rank: <mm:cloudinfo type="user" />/<mm:cloudinfo type="rank" />
        (must be administrator). Should not see this.
         <mm:import id="body2" />
      </mm:cloud>
      <mm:notpresent referid="body2">
          Skipped body. Should see this.
      </mm:notpresent>  
    </p>
  <hr />
  <a href="<mm:url page="cloud6.jsp" />">Previous</a><br />
  <a href="<mm:url page="cloud8.jsp" />">next</a><br />
  <a href="<mm:url page="index.jsp" />">back</a><br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
