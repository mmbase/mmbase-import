<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<cmsc:location var="cur" sitevar="site" />
<c:set var="page" value="${cur.id}" />

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
					<fmt:message key="edit_defaults.view" />
				</td>
				<td>
					<cmsc:select var="view">
						<c:forEach var="v" items="${views}">
							<cmsc:option value="${v.id}" name="${v.title}" />
						</c:forEach>
					</cmsc:select>
				</td>
			</tr>
         <tr>
				<td>
					<fmt:message key="edit_defaults.orderby" />
				</td>
				<td>
               <cmsc:select var="orderby">
		         	<cmsc:option value="title" message="edit_defaults.orderby.title" />
		         	<cmsc:option value="description" message="edit_defaults.orderby.description" />
			         <cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
			         <cmsc:option value="lastmodifieddate" message="edit_defaults.orderby.lastmodifieddate" />
			         <cmsc:option value="publishdate" message="edit_defaults.orderby.publishdate" />
			         <cmsc:option value="expirydate" message="edit_defaults.orderby.expirydate" />
	         	</cmsc:select>
				</td>
		  </tr>
	     <tr>
		     <td><fmt:message key="edit_defaults.direction" /></td>
		     <td>
              <cmsc:select var="direction">
                 <cmsc:option value="DOWN" message="edit_defaults.descending" />
			        <cmsc:option value="UP" message="edit_defaults.ascending" />
		        </cmsc:select>
           </td>
	     </tr>

        <tr>
		<td colspan="2">
			<h4><fmt:message key="edit_defaults.paging" /></h4>
		</td>
	</tr>
	<tr>
		<td ><fmt:message key="edit_defaults.usepaging" /></td>
		<td><cmsc:select var="usePaging" default="false">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td ><fmt:message key="edit_defaults.position" /></td>
		<td><cmsc:select var="position">
			<cmsc:option value="both" message="edit_defaults.position.both" />
			<cmsc:option value="top" message="edit_defaults.position.top" />
			<cmsc:option value="bottom" message="edit_defaults.position.bottom" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td ><fmt:message key="edit_defaults.elementsperpage" /></td>
		<td><cmsc:select var="elementsPerPage">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
			<cmsc:option value="25" />
			<cmsc:option value="50" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td ><fmt:message key="edit_defaults.numberofpages" /></td>
		<td><cmsc:select var="showPages">
			<cmsc:option value="" message="edit_defaults.unlimited" />
			<cmsc:option value="5" />
			<cmsc:option value="10" />
			<cmsc:option value="15" />
			<cmsc:option value="20" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td ><fmt:message key="edit_defaults.pagesindex" /></td>
		<td><cmsc:select var="pagesIndex">
			<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
			<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
			<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
		</cmsc:select></td>
	</tr>

			<tr>
				<td colspan="2">
					<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
				</td>
			</tr>
		</table>
		
	</form>
</div>