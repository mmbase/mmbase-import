<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<cmsc:portlet-preferences />
<cmsc:location var="location" />
<c:set var="page" value="${location.id}" />
<c:set var="newsletterid" value="${location.id}" />

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
	
	<h3><fmt:message key="edit_defaults.title" />${title}</h3>
	
	<form name="<portlet:namespace />form" method="post" target="_parent"
		action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
	
		<table class="editcontent">
			<tr>
				<td>
					<input type="hidden" name="page" value="${page}" />
					<fmt:message key="edit_defaults.available_newsletters" />
				</td>
				<td>
				                    <mm:cloud>
					<cmsc:multipleselect var="newsletters" size="5">
						<mm:listnodes type="newsletter">
							<mm:field name="number" id="number" write="false" />
							<mm:field name="title"  id="title" write="false" />
							<cmsc:multipleoption value="${number}" name="${title}" />
						</mm:listnodes>
					</cmsc:multipleselect>
					</mm:cloud>

				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="edit_defaults.window" />
				</td>
				<td>
					<cmsc:select var="window">
						<c:forEach var="position" items="${pagepositions}">
							<cmsc:option value="${position}" />
						</c:forEach>
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
				</td>
			</tr>
		</table>
		${themes}
	</form>
</div>