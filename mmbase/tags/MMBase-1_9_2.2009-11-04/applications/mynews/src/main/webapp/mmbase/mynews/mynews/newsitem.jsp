<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@page session="false"  language="java" contentType="text/html; charset=UTF-8" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content language="en" type="text/html" escaper="inline">
<mm:cloud>
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
  <link rel="stylesheet" type="text/css" href="<mm:url page="/mmbase/style/css/mmbase.css" />" />
 </head>
<body class="basic">
  <%-- use the title field again --%>
  <h1><mm:field  name="title"/></h1>
  <mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
  <div class="intro">
    <mm:field  name="intro" escape="p,links" />
  </div>
  <%--
    The default escape behaviour can be changed on tags producing output.
    escape="p"  generates p-tags, to escape newlines.
 --%>
  <mm:field  escape="none" name="body"/>
  <p>hallo</p>
  <table>
  <mm:relatednodes type="images" role="posrel"  orderby="posrel.pos" max="3">
   <mm:first><tr><th colspan="2">Related images</th></tr><tr><td /><td></mm:first>
   <img src="<mm:image template="s(200)" />" alt="<mm:field name="title" />" />
   <mm:last></td></tr></mm:last>
  </mm:relatednodes>

  <%-- the 'old' way to sort on posrel was like this (using 'clusternodes') --%>
  <mm:related path="posrel,urls" orderby="posrel.pos" max="3">
   <mm:first><tr><th colspan="2">Related urls</th></tr><tr><td /><td></mm:first>
   <a href="<mm:field name="urls.url"/>"><mm:field name="urls.description"/></a><br />
   <mm:last></td></tr></mm:last>
  </mm:related>

  <mm:relatednodes type="people">
   <mm:first><tr><th colspan="2">Authors</th></tr><tr><td /><td></mm:first>
   <em><mm:field name="firstname" /> <mm:field name="lastname" /></em><br />
   <mm:last></td></tr></mm:last>
  </mm:relatednodes>
  </table>

  <hr />
  <p>Belongs to <mm:node number="${_node}" jspvar="node"><jsp:expression>node.getNodeManager().getField("magazine").getDataType().getBaseType()</jsp:expression></mm:node></p>
  
  <mm:import externid="magid">default.mags</mm:import>
  <div class="link">
    <a href="<mm:url referids="magid" page="." /> "><img src="<mm:url page="/mmbase/style/images/back.gif" />" alt="back" /></a><br />
  </div>
  <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
</body>
</html>
</mm:node>
</mm:cloud>
</mm:content>
