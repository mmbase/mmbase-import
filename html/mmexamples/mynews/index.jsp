<%@page errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- get the current magazine, if there is no magazine use the default magazine with alias default.mags --%>
<mm:import externid="magid">default.mags</mm:import>

<%-- cloud tag --%>
<mm:cloud name="mmbase">
<%-- magazine node --%>
<mm:node number="$magid" id="mag">
<html>
 <head>
 <%-- we are in the magazine node  we can ask 
 for fields of this magazine --%>
 <title><mm:field  name="title"/></title>
 </head>
 <body background="../images/back.gif" text="#42BDAD" bgcolor="#00425B" link="#42BDAD" alink="#42BDAD" vlink="#42BDAD">
  <%-- use the title field again --%>
  <center>
  <table width="90%" cellspacing=1 cellpadding=3 border=0>
<tr>
  <td width="30"></td>
  <td bgcolor="#00425a" colspan="1" align="center">
  <h1><mm:field  name="title"/></h1>
  <h2><mm:field  name="subtitle"/></h3>
  </td>
</tr>
<tr>
  <td width="30"></td>
  <td bgcolor="#00425a" colspan="1">
  <b><mm:field  name="intro"/></b><br>
   <p>
  <mm:field  name="html(body)"/>
   </p>
  </td>
</tr>

  <%-- we are still in the magazine node, we can now aks for related news items  by using the related tag --%>
  <mm:relatednodes type="news">
   <%-- the first tag is a macro for <mm:listcondition value="first"/> --%>
   <mm:first><tr><td width="30"></td><td><table width="100%" cellspacing=1 cellpadding=3 border=0><tr><th align="LEFT">title</th><th align="RIGHT">link</th></tr></mm:first> 

   <%-- we now ask for a node field with name title, the magazine also has a title field
   when  there is only on nodeManager in a related or list tag the related tag acts like
   a node, so the tag wil return the title of the news item, if we still whant to get the 
   title of the magazine we wil need to add and id to the magazine tag (id="mag"). after that
   we can use <mm:field node="mag" name="title" --%>
   <tr>
    <td><mm:field name="title"/></td>
    <td align="right"><a href="newsitem.jsp?magid=<mm:field node="mag" name="number"/>&newsid=<mm:field name="number"/>">link</a></td>
   </tr> 
   <mm:last></table></td></tr></mm:last>
  </mm:relatednodes>
  </table>
  </center>
<hr />
<a href="<mm:url referids="magid" page="ordered.jsp" /> ">--&gt;  Ordered news</a><br />
<a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
