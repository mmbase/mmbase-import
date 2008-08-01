<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<div
	class="mm_c c_core b_connections ${requestScope.componentClassName}"
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
  </table>
  <p>
    <mm:link page="databases">
      <a href="${_}"><mm:link page="/mmbase/style/images/back.png"><img src="${_}" alt="back" width="21" height="20" /></mm:link></a>
      <a href="${_}">Return to Database Overview</a>
    </mm:link>
  </p>
</div>
</mm:cloud>
