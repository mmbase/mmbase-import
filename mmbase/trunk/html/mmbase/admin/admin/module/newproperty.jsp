<%@ page import="org.mmbase.bridge.*,java.util.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<mm:import externid="module" jspvar="module" />
<div
  class="component ${requestScope.className}"
  id="${requestScope.componentId}">
<h3>Administrate Module <%=module%>, New Property</h3>

<form action="<mm:url page="actions.jsp" referids="module" />" method="post">
<table summary="module property data" border="0" cellspacing="0" cellpadding="3">
<tr>
  <th>Property</th>
  <th>Value</th>
  <th class="view">Create</th>
</tr><tr>
  <td><input type="text" name="property" size="12" value="" /></td>
  <td><input type="text" name="value" size="62" value="" /></td>
<td class="linkdata">
    <input type="hidden" name="cmd" value="MODULE-SETPROPERTY" />
  <input type="image" src="<mm:url page="/mmbase/style/images/create.png" />" alt="Create" />
</td>
</tr>
</table>
</form>

<p>
  <mm:link page="modules-actions" referids="module">
	<a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
  </mm:link>
  Return to <mm:write referid="module" /> module
</p>

</div>
</mm:cloud>
