<%@page errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<%-- magazine node --%>
<%-- the page is called with a parameter newsid
we can use the parameter attribute of node to create a context 
for the MMBase node --%>
<mm:import externid="newsid" required="true" />
<mm:node number="$newsid">
<html>
 <head>
  <%-- we are in the news node  we can ask 
  for fields of this magazine --%>
  <title><mm:field  name="title"/></title>
  <link rel="stylesheet" type="text/css" href="style.css" />
 </head>
<body>
<center>
  <table width="90%" cellspacing=1 cellpadding=3 border=0>
  <tr>
  <th colspan="2">  
  <%-- use the title field again --%>
  <h2><mm:field  name="title"/></h2>
  </th><tr>
  <tr><td width="30" /><td>

  <b><mm:field  name="intro"/></B>

  <%-- it is possible to call MMBase functions on fields
  this i an example of converting text to html by adding
  brakes and paragraphs --%>
  <p><mm:field  name="html(body)"/></p>

  </td></tr>
  <mm:relatednodes type="images" max="3">
   <mm:first><tr><th colspan="2">Related images</th></tr><tr><td /><td></mm:first>
       <img src="<mm:image template="s(200)" />" alt="<mm:field name="title" />" />
   <mm:last></td></tr></mm:last>
  </mm:relatednodes>

  <mm:relatednodes type="urls">
   <mm:first><tr><th colspan="2">Related urls</th></tr><tr><td /><td></mm:first>
   <a href="<mm:field name="url"/>"><mm:field name="description"/></a><br />
   <mm:last></td></tr></mm:last>
  </mm:relatednodes>

  <mm:relatednodes type="people">
   <mm:first><tr><th colspan="2">Authors</th></tr><tr><td /><td></mm:first>
   <em><mm:field name="firstname" /> <mm:field name="lastname" /></em><br />
   <mm:last></td></tr></mm:last>
  </mm:relatednodes>
  </table>
</center>
<hr />
<mm:import externid="magid">default.mags</mm:import>
<a href="<mm:url referids="magid" page="index.jsp" /> ">back</a><br />
<a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
