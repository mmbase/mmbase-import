<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<div
  class="component mm_c_core mm_c_b_databases-connections ${requestScope.className}"
  id="${requestScope.componentId}">

<h3>Database connections overview</h3>

<table summary="database connections" border="0" cellspacing="0" cellpadding="3">
  <caption>
    This overview lists database connections.
  </caption>
  <tr>
    <th>Connection</th>
    <th>Database</th>
    <th>State</th>
    <th>Last Query</th>
    <th>Query #</th>
  </tr>
  <mm:nodelistfunction module="jdbc" name="CONNECTIONS">
    <tr>
      <td class="center"><mm:index /></td>
      <td><mm:field name="item1" /></td>
      <td><mm:field name="item2" /></td>
      <td><mm:field name="item3" /></td>
      <td><mm:field name="item4" /></td>
    </tr>
  </mm:nodelistfunction>
  <tr>
    <td>
      <mm:link page="databases" component="core">
        <a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
      </mm:link>
    </td>
    <td colspan="4">Return to Database Overview</td>
  </tr>
  </table>
</div>
</mm:cloud>
