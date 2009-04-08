<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.view" />:</td>
	<td>
		<cmsc:select var="view">
			<c:forEach var="v" items="${views}">
				<cmsc:option value="${v.id}" name="${v.title}" />
			</c:forEach>
		</cmsc:select>
	</td>
</tr>