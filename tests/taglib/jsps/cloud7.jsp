<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
  <head>

  </head>
 
    <h1>Cloud rank="administrator", no method, defaulting to "asis"</h1>
    <p>
      <mm:cloud rank="administrator" jspvar="cloud">
        Your rank: <%=cloud.getUser().getIdentifier()%>/<%=cloud.getUser().getRank() %>
        (must be administrator)
         <mm:import id="body" />
      </mm:cloud>
      <mm:notpresent referid="body">
          Skipped body
      </mm:notpresent>  
    </p>
    <h1>Cloud rank="administrator", no method, first logging out</h1>
    <p>
      <mm:cloud method="logout" />
      <mm:cloud rank="administrator" jspvar="cloud">
        Your rank: <%=cloud.getUser().getIdentifier()%>/<%=cloud.getUser().getRank() %>
        (must be administrator)
         <mm:import id="body2" />
      </mm:cloud>
      <mm:notpresent referid="body2">
          Skipped body
      </mm:notpresent>  
    </p>
  <hr />
  <a href="cloud6.jsp">Previous</a><br />
  <a href="cloud8.jsp">next</a><br />
  <a href="index.jsp">back</a><br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
