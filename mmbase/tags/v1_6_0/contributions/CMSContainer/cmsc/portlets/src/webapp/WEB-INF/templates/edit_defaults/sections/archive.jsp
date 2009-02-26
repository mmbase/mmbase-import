<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.archive" />:</td>
	<td><cmsc:select var="archive">
		<cmsc:option value="all" message="edit_defaults.archive.all" />
		<cmsc:option value="new" message="edit_defaults.archive.new" />
		<cmsc:option value="old" message="edit_defaults.archive.old" />
	</cmsc:select></td>
</tr>