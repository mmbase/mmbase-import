<%--
  Map this jsp to /rams (in web.xml):
  <servlet>
		<servlet-name>ram</servlet-name>
		<description>RAM's</description>
		<jsp-file>/mediaedit/view/concat.jsp</jsp-file>
		<load-on-startup>10</load-on-startup>
	</servlet>

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%response.setHeader("Content-Type", "audio/x-pn-realaudio");
%><mm:import externid="fragment" required="true" 
/><mm:cloud><mm:node  number="$fragment"
><mm:nodeinfo type="nodemanager"><mm:relatednodes type="$_" directions="destination" role="previous"
><mm:field name="url(rm)"  />
</mm:relatednodes><mm:field name="url(rm)" />
<mm:relatednodes type="$_" directions="source" role="previous"
><mm:field name="url(rm)" />
</mm:relatednodes></mm:nodeinfo></mm:node></mm:cloud>