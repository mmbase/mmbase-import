<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />

			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.address" />:</td>
				<td><input type="text" name="address" value="${address}" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.info" />:</td>
				<td><input type="text" name="info" value="${info}" /></td>
			</tr>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.key" />:</td>
				<td><input type="text" name="key" value="${key}" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.height" />:</td>
				<td><cmsc:text var="height" /></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.width" />:</td>
				<td><cmsc:text var="width" /></td>
			</tr>
	
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>