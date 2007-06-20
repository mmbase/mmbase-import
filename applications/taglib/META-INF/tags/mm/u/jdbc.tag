<%@tag import="java.sql.*, javax.sql.*, org.mmbase.module.core.MMBase, org.mmbase.storage.implementation.database.Attributes"
%>
<jsp:directive.attribute name="query"   required="true" />
<%
Connection con = null;
Statement stmt = null;
try {
DataSource dataSource = (DataSource) MMBase.getMMBase().getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
con = dataSource.getConnection();
stmt = con.createStatement();
ResultSet rs = stmt.executeQuery(org.mmbase.util.transformers.Xml.XMLUnescape(query));
%>
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
  <%
  while(true) {
   boolean valid = rs.next();
   if (! valid) break;
  %>
  <tr>
    <%
    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
    %>
    <td>
      <%= rs.getString(i) %>      
    </td>
    <%} %>
  </tr>
  <%}
  } catch (Exception e) {
    out.println("<tr><td colspan='100'>ERROR:" + e.getMessage() + "</td></tr>");
    out.println("<tr><td colspan='100'><pre>" + org.mmbase.util.logging.Logging.stackTrace(e) + "</pre></td></tr>");
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
</table>
