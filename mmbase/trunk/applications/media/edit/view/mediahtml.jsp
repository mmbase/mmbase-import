<%--
  This servlet produces pieces of html, and is meant to use in an
  object or perhaps an mm:include or so.


  Map this jsp to /mediahtml* (in web.xml):
  <servlet-mapping>
    <servlet-name>mediahtml</servlet-name>
    <url-pattern>/mediahtml*</url-pattern>
  </servlet-mapping>

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="../config/read.jsp" 
%><mm:import externid="type">link</mm:import>
<mm:import externid="fragment" required="true"  />
<mm:cloud>
<mm:node  number="$fragment">
url: <mm:field name="urls()" />
<mm:write referid="type">
  <mm:compare value="link">
    <a href="<mm:field name="url()" />"><mm:field name="title" /></a>
   </mm:compare>
   <mm:compare value="link_intro">
    <a href="<mm:field name="url()" />"><mm:field name="title" /></a> <mm:field name="html(intro)" />
   </mm:compare>
</mm:write>
</mm:node>
</mm:cloud>