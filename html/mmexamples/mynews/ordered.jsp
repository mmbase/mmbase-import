<%@page errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="magid">default.mags</mm:import>

<mm:cloud>
<mm:node number="$magid" id="mag">
<html>
 <head>
 <title><mm:field  name="title" /></title>
 <link rel="stylesheet" type="text/css" href="style.css" />
 </head>
 <body>
  <center>
  <table width="90%">
    <tr>
     <th widht="30" />
     <th align="center">
      <h1><mm:field  name="title" /></h1>
      <h2><mm:field  name="subtitle"/></h2>
    </th>
   </tr>
   <tr>
     <td width="30" />
     <td>
       <em><mm:field  name="intro"/></em><br />
       <p>
        <mm:field  name="html(body)"/>
      </p>
     </td>
   </tr>
   <%-- we have to use the related tag if we want to order with pos --%>
   <mm:related path="posrel,news" fields="posrel.pos,news.title" orderby="posrel.pos">
   <mm:first>
     <tr><td width="30" /><td><table width="100%"><tr><th align="left">title</th><th align="right">&nbsp;</th></tr></mm:first> 
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
