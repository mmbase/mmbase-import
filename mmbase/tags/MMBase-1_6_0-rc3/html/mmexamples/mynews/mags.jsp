<%@page errorPage="error.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- cloud tag --%>
<mm:cloud>
<html>
 <head>
 <title>Magazine Overview (MyNews examples)</title>
 <link rel="stylesheet" type="text/css" href="style.css" />
 </head>
 <body>
  <center>
  <table width="90%">
  <tr><th><h1>Magazine Overview</h1></th></tr>
  <mm:listnodes type="mags">
  <tr>
  <td><a href="<mm:url page="index.jsp"><mm:param name="magid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="title" /></a></td>
  </tr>
  </mm:listnodes>
  </table>
  </center>
  <hr />
    <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:cloud>
