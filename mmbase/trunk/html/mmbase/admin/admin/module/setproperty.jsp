<%@ page import="org.mmbase.bridge.*,java.util.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<mm:import externid="module" jspvar="module" />
<mm:import externid="property" jspvar="property" />
<div
  class="component ${requestScope.className}"
  id="${requestScope.componentId}">

<h3>Administrate Module <%=module%>, Property <%=property%></h3>

<% Module mmAdmin=ContextProvider.getDefaultCloudContext().getModule("mmadmin");
   String value=mmAdmin.getInfo("GETMODULEPROPERTY-"+module+"-"+property,request,response);
%>
<form action="<mm:url page="actions.jsp"/>" method="post">
<table summary="module property data">
<tr>
  <th>Property</th>
  <th>Value</th>
  <th class="view">Change</th>
</tr>
<tr>
  <td><%= property %></td>
  <td><input type="text" name="value" value="<%= value %>" /></td>
  <td class="view">
  <input type="hidden" name="module" value="<%= module %>" />
  <input type="hidden" name="property" value="<%= property %>" />
    <input type="hidden" name="cmd" value="MODULE-SETPROPERTY" />
  <input type="image" src="<mm:url page="/mmbase/style/images/change.png" />" alt="Change" />
  </td>
</tr>
</table>
</form>
</mm:cloud>
