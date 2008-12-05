<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">
<script type="text/javascript">
	function selectElement(contentelement, title) {
		document.forms['<portlet:namespace />form'].contentelement.value = contentelement;
		document.forms['<portlet:namespace />form'].contentelementtitle.value = title;
	}
	function selectPage(page, path, positions) {
		document.forms['<portlet:namespace />form'].page.value = page;
		document.forms['<portlet:namespace />form'].pagepath.value = path;
		
		var selectWindow = document.forms['<portlet:namespace />form'].window;
		for (var i = selectWindow.options.length -1 ; i >=0 ; i--) {
			selectWindow.options[i] = null;
		}
		for (var i = 0 ; i < positions.length ; i++) {
			var position = positions[i];
			selectWindow.options[selectWindow.options.length] = new Option(position, position);
		}
	}
	function erase(field) {
		document.forms['<portlet:namespace />form'][field].value = '';
	}
	function eraseList(field) {
		document.forms['<portlet:namespace />form'][field].selectedIndex = -1;
	}
</script>

<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" target="_parent"
	action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

<table class="editcontent">

	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.max_items" />:</td>
		<td><cmsc:select var="param_max">
			<cmsc:option value="100" message="edit_defaults.max_items_100" />
			<cmsc:option value="5" message="edit_defaults.max_items_5" />
			<cmsc:option value="10" message="edit_defaults.max_items_10" />
			<cmsc:option value="15" message="edit_defaults.max_items_15" />
			<cmsc:option value="20" message="edit_defaults.max_items_20" />
			<cmsc:option value="25" message="edit_defaults.max_items_25" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.order_by" />:</td>
		<td><cmsc:select var="param_orderBy">
			<cmsc:option value="count" message="edit_defaults.order_by_count" />
			<cmsc:option value="name" message="edit_defaults.order_by_name" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.window" />:</td>
		<td>
			<cmsc:select var="relatedWindow">
				<c:forEach var="position" items="${pagepositions}">
					<cmsc:option value="${position}" />
				</c:forEach>
			</cmsc:select>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<h4><fmt:message key="edit_defaults.clickpage" /></h4>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.page" />:</td>
		<td align="right">
			<a href="<c:url value='/editors/site/select/SelectorPage.do?channel=${page}' />"
				target="selectpage" onclick="openPopupWindow('selectpage', 340, 400)"> 
					<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.pageselect" />"/></a>
			<a href="javascript:erase('page');erase('pagepath');eraseList('window')">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/></a>
		</td>
		<td>
		<mm:cloud>
			<mm:node number="${page}" notfound="skip">
				<mm:field name="path" id="pagepath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="page" value="${page}" />
		<input type="text" name="pagepath" value="${pagepath}" disabled="true" />
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
	<tr>
		<td colspan="3">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
</table>
</form>
</div>