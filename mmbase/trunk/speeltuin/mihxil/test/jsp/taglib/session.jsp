<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
      %><%@ page import="java.util.*,org.mmbase.util.*,org.mmbase.cache.Cache" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
  </head>

  <body>
    <h1></h1>
    <mm:import externid="remove" from="parameters" />

    <mm:present referid="remove">
       <mm:write referid="remove" vartype="string" jspvar="attribute">
       <% session.removeAttribute(attribute); %>
       </mm:write>
    </mm:present>
    Total size of session: <%= SizeOf.getByteSize(session) %>
    <table width="100%" border="1" celpadding="1">
    <tr><th>Key</th><th>Value</th><th>Size</th><th /></tr>
    <% Enumeration e = session.getAttributeNames();
       while (e.hasMoreElements()) {
         String attribute = (String) e.nextElement(); %>
         <tr><td><%= attribute %></td><td><%=session.getAttribute(attribute)%></td><td><%=SizeOf.getByteSize(attribute) + SizeOf.getByteSize(session.getAttribute(attribute))%></td><td><a href="<mm:url><mm:param name="remove"><%=attribute%></mm:param></mm:url>">remove</a></td></tr>
      <% } %>
    </table>
    <hr />
    See also the <a href="caches.jsp?bytesize=true">Caches</a>
  </body>
</html>
