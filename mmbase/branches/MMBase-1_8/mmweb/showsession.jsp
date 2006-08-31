<table>
<%
if(request.getSession() == null) {
%>Session is null<%
} else {
for(java.util.Enumeration e = request.getSession().getAttributeNames(); e.hasMoreElements(); ) {
String name = (String) e.nextElement();
Object attr = request.getAttribute(name);
String cname = attr == null ? "null" : attr.getClass().getName();
%><tr><td><%=name%></td><td><%=cname%></td></tr><%
}
}
%>
</table>
<table>
<tr><th>name</th><th>version</th><th>maxage</th><th>domain</th><th>secure</th><th>path</th><th>comment</th><th>value</th></tr>
<%
Cookie[] c = request.getCookies();
for(int i=0; i < c.length; i++) {
Cookie cook = c[i];
%><tr>
<td><%=cook.getName()%></td>
<td><%=cook.getVersion()%></td>
<td><%=cook.getMaxAge()%></td>
<td><%=cook.getDomain()%></td>
<td><%=cook.getSecure()%></td>
<td><%=cook.getPath()%></td>
<td><%=cook.getComment()%></td>
<td><%=cook.getValue()%></td>
</tr><%
}
%>
</table>

