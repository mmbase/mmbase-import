<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
 
    <h1>Cloud rank="administrator", method="http"</h1>
    <p>
      <mm:cloud method="http" rank="administrator" jspvar="cloud">
        Your rank: <%=cloud.getUser().getIdentifier()%>/<%=cloud.getUser().getRank() %>
        (must be administrator)
         <mm:import id="body" />
      </mm:cloud>
      <mm:notpresent referid="body">
          Skipped body
      </mm:notpresent>  
    </p>
  <hr />
  <a href="<mm:url page="cloud7.jsp" />">Previous</a><br />
  <a href="<mm:url page="cloud9.jsp" />">next</a><br />
  <a href="<mm:url page="index.jsp" />">back</a><br />
    <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
  <hr />
  </body>
</html>
