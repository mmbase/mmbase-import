<%@page errorPage="error.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- get the current magazine, if there is no  magazine
use the default magazine with alias default.mags --%>
<% String magid = request.getParameter("magid") ; %>
<% if (magid == null ) magid = "default.mags" ; %>

<%-- cloud tag --%>
<mm:cloud name="mmbase">
<%-- magazine node --%>
<mm:node number="<%= magid %>" id="mag">
<HTML>
 <HEAD>
 <%-- we are in the magazine node  we can ask 
 for fields of this magazine --%>
 <TITLE><mm:field  name="title"/></TITLE>
 </HEAD>
 <BODY>
  <%-- use the title field again --%>
  <H1><mm:field  name="title"/></H1>

  <%-- we are still in the magazine node, we can now aks for related news items
  by using the related tag --%>
  <mm:related type="news">
   <%-- the first tag is a macro for <mm:listcondition value="first"/> --%>
   <mm:first><TABLE><TR><TH>title</TH><TH>link</TH></TR></mm:first> 

   <%-- we now ask for a node field with name title, the magazine also has a title field
   when  there is only on nodeManager in a related or list tag the related tag acts like
   a node, so the tag wil return the title of the news item, if we still whant to get the 
   title of the magazine we wil need to add and id to the magazine tag (id="mag"). after that
   we can use <mm:field node="mag" name="title" --%>
   <TR>
    <TD><mm:field name="title"/></TD>
    <TD><A HREF="newsitem.jsp?magid=<mm:field node="mag" name="number"/>&newsid=<mm:field name="number"/>">link</A></TD>
   </TR> 
   <mm:last></TABLE></mm:last>
  </mm:related>
 </BODY>
</HTML>
</mm:node>
</mm:cloud>
