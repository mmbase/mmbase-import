<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="3">
		<h4><fmt:message key="edit_defaults.clickpage" /></h4>
	</td>
</tr>

<tr>
	<td><fmt:message key="edit_defaults.page" />:</td>
	<td align="right">
		<a href="<c:url value='/editors/site/select/SelectorPage.do?channel=${page}' />" target="selectpage" onclick="openPopupWindow('selectpage', 340, 400)"> 
			<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.pageselect" />"/>
		</a>
		<a href="javascript:erase('page');erase('pagepath');eraseList('window')">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
		</a>
	</td>
	<td>
		<mm:cloud>
			<mm:node number="${page}" notfound="skip">
				<mm:field name="path" id="pagepath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="page" value="${page}" />
		<input type="text" name="pagepath" value="${pagepath}" disabled="true" />
	</td>
</tr>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
	<td>
		<cmsc:select var="window">
			<c:forEach var="position" items="${pagepositions}">
				<cmsc:option value="${position}" />
			</c:forEach>
		</cmsc:select>
	</td>
</tr>
