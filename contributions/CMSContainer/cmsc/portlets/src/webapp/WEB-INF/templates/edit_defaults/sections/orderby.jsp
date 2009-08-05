<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.orderby" />:</td>
	<td>
		<cmsc:select var="orderby">
			<cmsc:option value="" message="edit_defaults.orderby.channelposition" />
			<cmsc:option value="title" message="edit_defaults.orderby.title" />
			<cmsc:option value="description" message="edit_defaults.orderby.description" />
			<cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
			<cmsc:option value="lastmodifieddate" message="edit_defaults.orderby.lastmodifieddate" />
			<cmsc:option value="publishdate" message="edit_defaults.orderby.publishdate" />
			<cmsc:option value="expiredate" message="edit_defaults.orderby.expiredate" />
		</cmsc:select>
	</td>
</tr>
<tr>
	<td colspan="2"><fmt:message key="edit_defaults.direction" />:</td>
	<td>
		<cmsc:select var="direction">
			<cmsc:option value="DOWN" message="edit_defaults.descending" />
			<cmsc:option value="UP" message="edit_defaults.ascending" />
		</cmsc:select>
	</td>
</tr>