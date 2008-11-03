<%@taglib  uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@tag import="java.sql.*, javax.sql.*, java.util.*, org.mmbase.module.core.MMBase, org.mmbase.storage.implementation.database.Attributes"
%>
<jsp:directive.attribute name="query"   required="true" />
<jsp:directive.attribute name="mode"   />
<jsp:directive.attribute name="max" type="java.lang.Integer"  />
<jsp:directive.attribute name="throwexception" type="java.lang.Boolean"  />
<%
Connection con = null;
Statement stmt = null;
try {
DataSource dataSource = (DataSource) MMBase.getMMBase().getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
con = dataSource.getConnection();
stmt = con.createStatement();
query = query.replace("$PREFIX", MMBase.getMMBase().getBaseName());
ResultSet rs = stmt.executeQuery(org.mmbase.util.transformers.Xml.XMLUnescape(query));
%>
<c:if test="${empty mode or mode eq 'table'}">
<table>
  <tr>
    <%
    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)  {
    %>
    <th>
      <%= rs.getMetaData().getColumnName(i) %>
    </th>
    <%} %>
  </tr>
  </c:if>
  <%
  int seq = 0;
  while(true) {
   boolean valid = rs.next();
   seq ++;
   if (max != null && seq > max) break;
   if (! valid) break;
  %>
<c:if test="${empty mode or mode eq 'table'}">
  <tr>
    <%
    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    %>
    <td>
      <%= rs.getString(i) %>
    </td>
    <%} %>
  </tr>
  </c:if>
  <c:if test="${mode eq 'nodes'}">
	<%
	Map data = new HashMap();
         for	(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
	   data.put(rs.getMetaData().getColumnName(i), rs.getString(i));
	 }
	 jspContext.setAttribute("help_node", data);
    %>
    <mm:node referid="help_node">
      <jsp:doBody />
  </mm:node>
  </c:if>
  <%}
  } catch (Exception e) {
     if (throwexception != null && throwexception) throw e;
      out.println("<tr><td colspan='100'>ERROR:" + e.getMessage() + "</td></tr>");
      out.println("<tr><td colspan='100'><pre>" + org.mmbase.util.logging.Logging.stackTrace(e) +  "</pre></td></tr>");
  } finally {
  try {
  if (stmt != null) {
  stmt.close();
  }
  } catch (Exception g) {}
  try {
  if (con != null) {
  con.close();
  }
  } catch (Exception g) {}
  }
  %>
<c:if test="${empty mode or mode eq 'table'}">
</table>
</c:if>
