<%@page session="false" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" escaper="inline" type="text/html">

<%-- MMBase data can only be used inside a cloud tag --%>
<mm:cloud>

<%-- This whole page is base on the pool node --%>
 <html>
 <head>
    <title>Search results</title>
    <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
 </head>
 <body>
  <table width="90%" align="center">
   <tr>
    <th width="30" />
    <th align="center">
      <h1>Search results</h1>
    </th>
   </tr>

  <%-- The 'listnodescontainer' tag is new in MMBase 1.7. It allows you to
       create a query without any knowledge of the underlying search language
       (usually SQL). In this example you see that we use the same 'fieldlist'
       as in the index.jsp file, but now we have a 'fieldinfo' tag that has
       type 'usesearchinput'. This tag will feed constraints to the 
       surrounding listnodescontainer. --%>
       
  <mm:listnodescontainer type="urls">
    <mm:fieldlist nodetype="urls" fields="description">
      <mm:fieldinfo type="usesearchinput" />
    </mm:fieldlist>
    
    <%-- Executing the final query is as simple as just doing a 'listnodes' in this
         surrounding listnodescontainer! Searching was never this simple --%>
       
    <mm:listnodes>
    <mm:first><!-- a header for the urls overview, only shown if there are URLs at all -->
      <tr><td width="30" />
      <td><table width="100%">
      <tr><th align="left">Urls that matched your query</th><th align="right">&nbsp;</th></tr>
    </mm:first> 
    <tr>
      <td class="link">
       <a href="<mm:field name="url" />"><mm:field name="url" /></a> <br>
       <mm:field name="description" />
      </td> 
    </tr> 
    <mm:last></table></td></tr></mm:last>
    </mm:listnodes>
  </mm:listnodescontainer>  
  </table>

  <hr />
  <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:cloud>
</mm:content>