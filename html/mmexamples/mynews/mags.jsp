<%@page errorPage="error.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- cloud tag --%>
<mm:cloud>
<html>
 <head>
 <title>Magazine Overview (MyNews examples)</title>
 </head>
 <body background="../images/back.gif" text="#42BDAD" bgcolor="#00425B" link="#42BDAD" alink="#42BDAD" vlink="#42BDAD">
  <center>
   <h1>Magazine Overview</h1>
  <table width="90%" cellspacing=1 cellpadding=3 border=0>
  <mm:listnodes type="mags">
  <tr>
  <td width="30"><a href="<mm:url page="index.jsp"><mm:param name="magid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="title" /></a></td>
  </tr>
  </mm:listnodes>
  </table>
  </center>
<hr />
<a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:cloud>
