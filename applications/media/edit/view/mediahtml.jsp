<%--
  Map this jsp to /rams (in web.xml):
  <servlet>
		<servlet-name>mediahtml</servlet-name>
		<description>HTML pieces</description>
		<jsp-file>/mediaedit/view/mediahtml.jsp</jsp-file>
		<load-on-startup>11</load-on-startup>
	</servlet>

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ include file="../config/read.jsp" 
%><mm:import externid="type">link</mm:import>
<mm:import externid="fragment" required="true"  />
<mm:cloud>
<mm:node  number="$fragment">
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