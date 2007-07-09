<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@page errorPage="error.jsp" session="false" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" escaper="inline" type="text/html">
<mm:cloud>
<html>
 <head>
   <title>Magazine Overview (MyNews examples)</title>
   <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
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
  <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>
</mm:cloud>
</mm:content>
