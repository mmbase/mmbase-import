<%@page errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="magid">default.mags</mm:import>

<mm:cloud>
<mm:node number="$magid" id="mag">
<html>
 <head>
 <title><mm:field  name="title" /></title>
 </head>
 <body background="../images/back.gif" text="#42BDAD" bgcolor="#00425B" link="#42BDAD" alink="#42BDAD" vlink="#42BDAD">
  <center>
  <table width="90%" cellspacing=1 cellpadding=3 border=0>
<tr>
  <td width="30"></td>
  <td bgcolor="#00425a" colspan="1" align="center">
  <h1><mm:field  name="title" /></h1>
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

  <%-- we have to use the related tag if we want to order with pos --%>
  <mm:related path="posrel,news" fields="posrel.pos,news.title" orderby="posrel.pos">
   <%-- the first tag is a macro for <mm:listcondition value="first"/> --%>
   <mm:first>
     <tr><td width="30"></td><td><table width="100%" cellspacing="1" cellpadding="3" border="0"><tr><th align="LEFT">title</th><th align="RIGHT">link</th></tr></mm:first> 
   <tr>
    <td><mm:field name="news.title" /></td>
    <td align="right"><a href="<mm:url referids="mag" page="newsitem.jsp"><mm:param name="newsid"><mm:field name="news.number"/></mm:param></mm:url>">link</a></td>
   </tr> 
   <mm:last></table></td></tr></mm:last>
  </mm:related>
  </table>
  </center>
<hr />
<a href="<mm:url referids="magid" page="index.jsp" /> "> &lt;--  Simple news</a><br />
<a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
