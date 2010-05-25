<%@ page import="org.mmbase.module.core.MMBase"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<mm:cloud rank="administrator">
<div
  class="mm_c c_core b_databases ${requestScope['org.mmbase.componentClassName']}"
  id="${requestScope['org.mmbase.componentId']}">
<h3>${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.title)}</h3>
<%
if (org.mmbase.module.core.MMBase.getMMBase().getStorageManagerFactory() != null) {
%>
<table>
  <caption>Configuration</caption>
<tr><th>Storage Factory</th><td><jsp:expression>MMBase.getMMBase().getStorageManagerFactory().getClass().getName()</jsp:expression></td></tr>
<tr><th>Configuration:</th><td><jsp:expression>MMBase.getMMBase().getStorageManagerFactory().getDocumentReader().getSystemId()</jsp:expression></td></tr>
<% if (MMBase.getMMBase().getStorageManagerFactory() instanceof org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory) { %>
<tr><th>DataSource:</th><td><jsp:expression>((org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory) MMBase.getMMBase().getStorageManagerFactory()).getDataSource()</jsp:expression></td></tr>
<tr><th>Connection:</th><td>
<% java.sql.Connection con = ((org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory) MMBase.getMMBase().getStorageManagerFactory()).getDataSource().getConnection(); %>
<jsp:expression>con.getClass().getName()</jsp:expression>
<% con.close(); %>
</td></tr>
<% } %>
<tr><th>Storage Implementation</th><td><jsp:expression>MMBase.getMMBase().getStorageManagerFactory().getStorageManager().getClass().getName()</jsp:expression></td></tr>
<tr><th>Search Query Handler:</th><td><jsp:expression>MMBase.getMMBase().getStorageManagerFactory().getSearchQueryHandler().getClass().getName()</jsp:expression></td></tr>
<tr><th>Attributes:</th><td>
<table>
  <% pageContext.setAttribute("attributes", MMBase.getMMBase().getStorageManagerFactory().getAttributes()); %>
  <c:forEach items="${attributes}" var="entry">
    <tr><th><mm:write value="${entry.key}" /></th><td><mm:write value="${entry.value}" /></td></tr>
  </c:forEach>
</table>
</td></tr>
</table>
<% } %>
<table summary="databases" border="0" cellspacing="0" cellpadding="3">
  <caption>${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.description)}</caption>
<%
java.util.Map params = new java.util.Hashtable();
if (org.mmbase.module.core.MMBase.getMMBase().getStorageManagerFactory() == null) {
%>
<tr>
  <th>Name</th>
  <th>Version</th>
  <th>Installed</th>
  <th>Maintainer</th>
  <th>View</th>
</tr>
<mm:nodelistfunction module="mmadmin" name="DATABASES">
  <tr>
    <td><mm:field id="database" name="item1" /></td>
    <td><mm:field name="item2" /></td>
    <td><mm:field name="item3" /></td>
    <td><mm:field name="item4" /></td>
    <td class="view">
      <mm:link page="databases-connections">
        <a href="${_}"><img src="${mm:link('/mmbase/style/images/search.png')}" alt="view" width="21" height="20" /></a>
      </mm:link>
    </td>
</tr>
</mm:nodelistfunction>
<% } %>

<mm:hasfunction module="jdbc" name="POOLS">
<tr>
  <th colspan="2">Pool name</th>
  <th>Size</th>
  <th>Connections created</th>
  <th>View</th>
</tr>
<mm:nodelistfunction module="jdbc" name="POOLS">
  <tr>
    <td colspan="2">
      <mm:field name="item1" id="item1" write="false">
		<mm:link page="databases-connections">
		  <a href="${_}" title="view connections"><mm:write referid="item1" /></a>
		</mm:link>
      </mm:field>
    </td>
    <td><mm:field name="item2" /></td>
    <td><mm:field name="item3" /></td>
    <td class="view">
      <mm:link page="databases-connections">
        <a href="${_}" title="view connections"><img src="${mm:link('/mmbase/style/images/search.png')}" alt="view" width="21" height="20" /></a>
      </mm:link>
    </td>
  </tr>
</mm:nodelistfunction>
</mm:hasfunction>

<mm:hasfunction module="jdbc" name="POOLS" inverse="true">
  <tr>
    <td colspan="5">Function for database pool inspection not available</td>
  </tr>
</mm:hasfunction>

</table>
</div>
</mm:cloud>
