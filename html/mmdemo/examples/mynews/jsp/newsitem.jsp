<%@page errorPage="error.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<%-- magazine node --%>
<%-- the page is called with a parameter newsid
we can use the parameter attribute of node to create a context 
for the MMBase node --%>
<mm:node number="param:newsid">
<HTML>
 <HEAD>
  <%-- we are in the news node  we can ask 
  for fields of this magazine --%>
  <TITLE><mm:field  name="title"/></TITLE>
 </HEAD>
 <BODY>
  <%-- use the title field again --%>
  <H1><mm:field  name="title"/></H1>

  <B><mm:field  name="intro"/></B>

  <%-- it is possible to call MMBase functions on fields
  this i an example of converting text to html by adding
  brakes and paragraphs --%>
  <P><mm:field  name="html(body)"/></P>

  <mm:related type="images">
   <mm:first>Related images<BR></mm:first>
    <IMG SRC="/img.db?<mm:field name="number"/>+(200)">
  </mm:related>

  <mm:related type="urls">
   <mm:first>Related urls<BR></mm:first>
   <A HREF="<mm:field name="url"/>"><mm:field name="description"/></A><BR>
  </mm:related>
 </BODY>
</HTML>
</mm:node>
</mm:cloud>
