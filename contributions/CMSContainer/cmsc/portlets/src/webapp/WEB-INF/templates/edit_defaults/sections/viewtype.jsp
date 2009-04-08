<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.viewtype" />:</td>
	<td>
		<cmsc:select var="viewtype">
			<cmsc:option value="oneDetail" message="edit_defaults.viewtype.oneDetail" />
			<cmsc:option value="list" message="edit_defaults.viewtype.list" />
			<cmsc:option value="detail" message="edit_defaults.viewtype.detail" />
		</cmsc:select>
	</td>
</tr>