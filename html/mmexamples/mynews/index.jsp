<%@page session="false" errorPage="error.jsp" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" escaper="inline" type="text/html">

<%-- get the current magazine, if there is no magazine use the default magazine with alias default.mags --%>
<mm:import externid="magid">default.mags</mm:import>

<%-- MMBase data can only be used inside a cloud tag --%>
<mm:cloud>

<%-- This whole page is base on a magazine node --%>
<mm:node number="$magid" id="mag">
 <html>
 <head>
    <%-- we are in the magazine node  we can ask for fields of this magazine --%>
    <title><mm:field  name="title"/></title>
    <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
 </head>
 <body>
  <table width="90%" align="center">
   <tr>
    <th width="30" />
    <th align="center">
      <h1><mm:field  name="title"/></h1>
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
  <%-- we are still in the magazine node, we can now ak for related news items  by using the relatednodes tag --%>
  <mm:relatednodes type="news" orderby="number"><%-- simply ordered on age --%>
    <mm:first><!-- a header for the news overview, only shown if there is news at all -->
      <tr><td width="30" />
      <td><table width="100%">
      <tr><th align="left">title</th><th align="right">&nbsp;</th></tr>
    </mm:first> 
    <%-- we now ask for a node field with name title, the magazine also has a title field
    when  there is only on nodeManager in a related or list tag the related tag acts like
    a node, so the tag wil return the title of the news item, if we still whant to get the 
    title of the magazine we wil need to add and id to the magazine tag (id="mag"). after that
    we can use <mm:field node="mag" name="title" --%>
    <tr>
      <td><mm:field name="title"/></td>
      <td align="right" class="link"><a href="<mm:url referids="magid" page="newsitem.jsp" ><mm:param name="newsid"><mm:field name="number"/></mm:param></mm:url>"><img src="<mm:url page="/mmbase/style/images/next.gif" />" alt="link" /></a></td>
    </tr> 
    <mm:last></table></td></tr></mm:last>
  </mm:relatednodes>
  </table>
  <hr />
  <div class="link">
   <a href="<mm:url referids="magid" page="ordered.jsp" /> ">Ordered news <img src="<mm:url page="/mmbase/style/images/next.gif" />"></a><br />
  </div> 
  <hr /> 
   <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
</mm:content>
