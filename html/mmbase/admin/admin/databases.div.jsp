<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">
<div
  class="component mm_c_core mm_c_b_databases ${requestScope.className}"
  id="${requestScope.componentId}">
<h3>Database Overview</h3>
<table summary="databases" border="0" cellspacing="0" cellpadding="3">
  <caption>
    This overview lists all database systems supported by this system, as well as
    all connection pools (which administrate the actual database connections).
  </caption>
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
      <a href="<mm:url referids="database" page="database/actions.jsp" />">
        <img src="<mm:url page="/mmbase/style/images/search.png" />" border="0" alt="view" />
      </a>
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
    <td colspan="2"><mm:field name="item1" id="item1" /></td>
    <td><mm:field name="item2" /></td>
    <td><mm:field name="item3" /></td>
    <td class="view">
      <mm:link page="databases-connections" referids="item1" component="core">
        <a href="${_}"><img src="<mm:url page="/mmbase/style/images/search.png" />" border="0" alt="view" /></a>
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
