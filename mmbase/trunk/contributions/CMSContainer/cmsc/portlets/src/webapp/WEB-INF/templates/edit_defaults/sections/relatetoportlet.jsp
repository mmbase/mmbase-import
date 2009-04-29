<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="3">
		<h4><fmt:message key="edit_defaults.relateportlet" /></h4>
	</td>
</tr>

<tr>
	<td><fmt:message key="edit_defaults.page" />:</td>
	<td align="right">
		<a href="<c:url value='/editors/site/select/SelectorRelatedpage.do?channel=${relatedPage}' />" target="selectpage" onclick="openPopupWindow('selectpage', 340, 400)"> 
			<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.pageselect" />"/>
		</a>
		<a href="javascript:erase('relatedPage');erase('relatedPagepath');eraseList('relatedWindow')">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
		</a>
	</td>
	<td>
		<mm:cloud>
			<mm:node number="${relatedPage}" notfound="skip">
				<mm:field name="path" id="relatedPagepath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="relatedPage" value="${relatedPage}" />
		<input type="text" name="relatedPagepath" value="${relatedPagepath}" disabled="true" />
	</td>
</tr>

<tr>
	<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
	<td>
		<cmsc:select var="relatedWindow">
			<c:forEach var="position" items="${relatedpagepositions}">
				<cmsc:option value="${position}" />
			</c:forEach>
		</cmsc:select>
	</td>
</tr>
