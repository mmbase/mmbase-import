<%@page session="false" errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html"  escaper="inline">
<mm:import externid="magid">default.mags</mm:import>
<mm:cloud>
<mm:node number="$magid" id="mag">
<html>
 <head>
   <title><mm:field  name="title" /></title>
   <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
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
       <mm:field  escape="p" name="body"/>
     </td>
   </tr>
   <%-- we have to use the related tag if we want to order with pos --%>
   <mm:relatednodes id="newsid" role="posrel" type="news" orderby="posrel.pos">
     <mm:first>
       <tr><td width="30" /><td><table width="100%"><tr><th align="left">title</th><th align="right">&nbsp;</th></tr></mm:first> 
       <tr>
         <td><mm:field name="title" /></td>
         <td align="right"><a href="<mm:url referids="mag,newsid" page="newsitem.jsp" />">link</a></td>
       </tr> 
       <mm:last></table></td></tr></mm:last>
     </mm:relatednodes>
  </table>
  </center>
  <hr />
    <a href="<mm:url referids="magid" page="index.jsp" /> "> &lt;--  Simple news</a><br />
    <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
</mm:content>
