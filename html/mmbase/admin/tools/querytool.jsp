<%@page session="false" import="java.sql.*, javax.sql.*, org.mmbase.module.core.MMBase, org.mmbase.storage.implementation.database.Attributes"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html" postprocessor="reducespace" expires="0">
<mm:cloud rank="administrator">
<html>
  <head>
    <title>MMBase SQL tool (Know what you're doing !)</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css">
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
  </head>
  <body>
    <%
    if (false) {
    %>
    <mm:import externid="submit" />
    <mm:import externid="query" jspvar="sqlString" vartype="string" />
    <form method="post">
      <textarea name="query" style="width: 100%; height: 20ex;"><mm:write referid="query" /></textarea>
      <input type="submit" name="submit" />
    </form>
    <mm:present referid="submit">
      <table>
        <%
        Connection con = null;
        Statement stmt = null;
        try {
        DataSource dataSource = (DataSource) MMBase.getMMBase().getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
        con = dataSource.getConnection();
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlString);
        %>
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
        while(rs.next()) {
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
        out.println(e.getMessage());
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
    </mm:present>
    <%} else {%>
    <h1>Disabled, change JSP to switch this feature on</h1>
    <% } %>
  </body>
</html>
</mm:cloud>
</mm:content>
