<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<div class="portlet-config-canvas">
<c:if test="${not empty definitions}">
	<h3><fmt:message key="edit_defaults.createportlet" /></h3>
	<form name="<portlet:namespace />create_form" method="post" target="_parent"
		action="<cmsc:actionURL><cmsc:param name="action" value="create"/></cmsc:actionURL>">
	
	<table class="editcontent">
		<tr>
			<td>
				<fmt:message key="edit_defaults.definition" />:
			</td>
			<td>
				<script type="text/javascript">
				function <portlet:namespace />setViews(el, viewsSelectBox) {
					var definitions = [];
					<c:forEach var="p" items="${views}">
						var def = {id: '${p.key.id}'}
						definitions[definitions.length] = def;
						def.views = [];
						<c:forEach var="v" items="${p.value}">
							var view = {id: '${v.id}', title: '${v.title}' }
							def.views[def.views.length] = view;
						</c:forEach>
					</c:forEach>
	
					var selectViews = document.getElementById(viewsSelectBox);
					for (var i = selectViews.options.length -1 ; i >=0 ; i--) {
						var option = selectViews.options[i] = null;
					}
	
					for (var i = 0 ; i < definitions.length ; i++) {
						var definition = definitions[i];
						if (definition.id == el.value) {
							for (var j = 0 ; j < definition.views.length ; j++) {
								var view = definition.views[j];
								selectViews.options[selectViews.options.length] = new Option(view.title, view.id);
							}
						}
					}
					if (selectViews.options.length == 0) {
						selectViews.options[selectViews.options.length] = new Option(' - ', '');
					}
				}
				</script>
			
				<select name="definitionname" onchange="<portlet:namespace />setViews(this, '<portlet:namespace />views');" id="<portlet:namespace />definition">
					<option value=""> - </option>
					<c:forEach var="d" items="${definitions}">
						<option value="${d.id}">${d.title}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="edit_defaults.view" />:
			</td>
			<td>
	
				<select name="view" id="<portlet:namespace />views">
					<option value=""> - </option>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<a href="javascript:document.forms['<portlet:namespace />create_form'].submit()" class="button">
					<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.create" /></a>
			</td>
		</tr>
	</table>
	</form>
</c:if>
<c:if test="${not empty portlets}">
	<h3><fmt:message key="edit_defaults.selectportlet" /></h3>
	
	<form name="<portlet:namespace />select_form" method="post" target="_parent"
		action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
	<table class="editcontent">
		<tr>
			<td>
				<fmt:message key="edit_defaults.portlet" />:
			</td>
			<td>
				<select name="portlet" id="<portlet:namespace />portlet">
					<option value=""> - </option>
					<c:forEach var="p" items="${portlets}">
						<option value="${p.id}">${p.title}</option>
					</c:forEach>
				</select> 
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<a href="javascript:document.forms['<portlet:namespace />select_form'].submit()" class="button">
					<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.select" /></a>
			</td>
		</tr>
	</table>
	</form>
</c:if>
</div>