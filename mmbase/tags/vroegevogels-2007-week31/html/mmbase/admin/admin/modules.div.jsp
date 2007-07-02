<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<div
  class="mm_c mm_c_core mm_c_b_servers ${requestScope.componentClassName}"
  id="${requestScope.componentId}">
<h3>Module Overview</h3>

<table summary="modules" border="0" cellspacing="0" cellpadding="3">
  <caption>This overview lists all modules known to this system.</caption>
  <tr>
	<th>Name</th>
	<th>Version</th>
	<th>Installed</th>
	<th>Maintainer</th>
	<th class="center">Manage</th>
  </tr>
  <mm:nodelistfunction module="mmadmin" name="MODULES">  
  <tr>
	<td>
	  <mm:link page="modules-actions">
	    <mm:param name="module"><mm:field name="item1" /></mm:param>
		<a title="view module" href="${_}"><mm:field name="item1" /></a>
	  </mm:link>
	</td>
	<td><mm:field name="item2" /></td>
	<td><mm:field name="item3" /></td>
	<td><mm:field name="item4" /></td>
	<td class="center">
	  <mm:link page="modules-actions">
	    <mm:param name="module"><mm:field name="item1" /></mm:param>
		<a title="view module" href="${_}"><img src="<mm:url page="/mmbase/style/images/next.png" />" alt="view" /></a>
	  </mm:link>
	</td>
  </tr>
  </mm:nodelistfunction>
</table>
</div>
</mm:cloud>
