<%@page errorPage="error.jsp"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
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
 <BODY BACKGROUND="../images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#42BDAD" ALINK="#42BDAD" VLINK="#42BDAD">
  <%-- use the title field again --%>
  <CENTER>
  <TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
  <TD WIDTH="30"></TD>
  <TD BGCOLOR="#00425A" COLSPAN="1" ALIGN="CENTER">
  <H1><mm:field  name="title"/></H1>
  <H2><mm:field  name="subtitle"/></H3>
  </TD>
</TR>
<TR>
  <TD WIDTH="30"></TD>
  <TD BGCOLOR="#00425A" COLSPAN="1">
  <B><mm:field  name="intro"/></B><BR>
   <P>
  <mm:field  name="html(body)"/>
   </P>
  </TD>
</TR>

  <%-- we are still in the magazine node, we can now aks for related news items
  by using the related tag --%>
  <mm:related type="news">
   <%-- the first tag is a macro for <mm:listcondition value="first"/> --%>
   <mm:first><TR><TD WIDTH="30"></TD><TD><TABLE width="100%" cellspacing=1 cellpadding=3 border=0><TR><TH ALIGN="LEFT">title</TH><TH ALIGN="RIGHT">link</TH></TR></mm:first> 

   <%-- we now ask for a node field with name title, the magazine also has a title field
   when  there is only on nodeManager in a related or list tag the related tag acts like
   a node, so the tag wil return the title of the news item, if we still whant to get the 
   title of the magazine we wil need to add and id to the magazine tag (id="mag"). after that
   we can use <mm:field node="mag" name="title" --%>
   <TR>
    <TD><mm:field name="title"/></TD>
    <TD ALIGN="RIGHT"><A HREF="newsitem.jsp?magid=<mm:field node="mag" name="number"/>&newsid=<mm:field name="number"/>">link</A></TD>
   </TR> 
   <mm:last></TABLE></TD></TR></mm:last>
  </mm:related>
  </TABLE>
  </CENTER>
 </BODY>
</HTML>
</mm:node>
</mm:cloud>
