<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
  %><%@ page import="java.util.*" %><%@ page import="org.mmbase.cache.Cache" %>
<html>
<head><title>MMBase Caches</title></head>
<body>
<mm:import externid="cache" from="parameters" />


<mm:notpresent referid="cache">

<h1>MMBase Caches</h1>

<table width="100%" border="1" celpadding="1">
<tr><th>Cache</th><th>Size</th><th>Hits</th><th>Misses</th><th>Content</th></tr>
<% Iterator i = org.mmbase.cache.Cache.getCaches().iterator();
   while (i.hasNext()) {     
      Cache cache = Cache.getCache((String) i.next());
      out.println("<tr><td>" + cache.getDescription() + " (" + cache.getName() + ")</td><td align=\"right\">" + cache.getSize() + " </td><td align=\"right\">" + cache.getHits() + " </td><td align=\"right\">" + cache.getMisses() + " </td>");
%>
<td align="center"><a href="<mm:url><mm:param name="cache"><%= cache.getName() %></mm:param></mm:url>">show</a></td></tr>
<%

  }
%>
</table>
</mm:notpresent>

<mm:present referid="cache">
<mm:write referid="cache" jspvar="cacheName" vartype="String">
<% Cache cache = Cache.getCache(cacheName); 
  if (cache != null) {
%>
<h1><%= cache.getDescription() %> Cache</h1>
<table width="100%" border="1" celpadding="1">
<tr><th>Key</th><th>Value</th></tr>
<%
    Enumeration e = cache.getOrderedElements();
    while (e.hasMoreElements()) {
        out.println("<tr><td></td><td>" + e.nextElement() + "</td></tr>");
    }
}
%>
</table>
<hr />
</mm:write>
<a href="<mm:url />">Back</a>

</mm:present>
<hr />
<a href="<mm:url page="/" />">Home </a>

</body>
</html>
