<%@page session="false" language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" escaper="inline" type="text/html">

<%-- get the current pool, if there is no pool use the default magazine with alias default.mags --%>
<mm:import externid="poolid">MyYahoo.start</mm:import>

<%-- MMBase data can only be used inside a cloud tag --%>
<mm:cloud>

<%-- This whole page is base on the pool node --%>
<mm:node number="$poolid" id="pool">
 <html>
 <head>
    <%-- we are in the pool node  we can ask for fields of this pool--%>
    <title><mm:field  name="name"/></title>
    <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
 </head>
 <body>
  <table width="90%" align="center">
   <tr>
    <th width="30" />
    <th align="center">
      <h1><mm:field  name="name"/></h1>
      <mm:field  name="description"/>
    </th>
  </tr>

  <mm:relatednodes type="pools" orderby="number"><%-- simply ordered on age --%>
    <mm:first>
    <!-- a header for the subcategories overview, only show if there are categories at all -->
  <tr>
    <td width="30" />
    <td>
      <table width="100%">
      <tr>
        <th align="left" colspan="2">Sub categories</th>
      </tr>
    </mm:first>

    <tr>
    <td colspan="2">
      <%-- We use the 'mm:url' tag with a nested 'mm:param' to create a link --%>
      <a href="<mm:url><mm:param name="poolid"><mm:field name="number" /></mm:param></mm:url>">
      <mm:field name="name" />
      </a>
    </td>
    </tr>

    <mm:last>
      </table>
    </td>
  </tr>  
    </mm:last>
  </mm:relatednodes>
  
  <%-- we are still in the pool node, we can now ask for related URLs by using the relatednodes tag --%>
  <mm:relatednodes type="urls" orderby="number"><%-- simply ordered on age --%>
    <mm:first><!-- a header for the urls overview, only shown if there are URLs at all -->
      <tr><td width="30" />
      <td><table width="100%">
      <tr><th align="left">URLs in this category</th><th align="right">&nbsp;</th></tr>
    </mm:first> 
    <tr>
      <td class="link">
       <a href="<mm:field name="url" />"><mm:field name="url" /></a> <br>
       <mm:field name="description" />
      </td> 
    </tr> 
    <mm:last></table></td></tr></mm:last>
  </mm:relatednodes>
  </table>
  <hr />
  <div align="right">
    <form method="post" action="search.jsp">
      Search for URLs: 
      <%-- This is the preferred way to add 'search input' fields to a form: 
           we allow MMBase to handle the generation of searchlanguage (SQL)
           later in the 'search.jsp' page --%>
      <mm:fieldlist nodetype="urls" fields="description">
        <mm:fieldinfo type="searchinput" options="extra:style=width:100px" />
      </mm:fieldlist>
      <input type="image" src="<mm:url page="/mmbase/style/images/search.gif" />" />
    </form>
  </div>
    
  <hr />
  <div class="link">
    <a href="<mm:url page=".." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
  <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
 </body>
</html>
</mm:node>
</mm:cloud>
</mm:content>
