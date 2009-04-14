<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

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
                <td colspan="3"><h4><fmt:message key="edit_defaults.unsubscribe.subject" /></h4></td>
            </tr>
           <tr>
              <td><fmt:message key="edit_defaults.unsubscribe.emailfromname" />:</td>
              <td></td>
              <td>
                 <input type="text" name="emailFromName" value="${fn:escapeXml(emailFromName)}" />
              </td>
           </tr>
           <tr>
              <td><fmt:message key="edit_defaults.unsubscribe.emailfromemail" />:</td>
              <td></td>
              <td>
                 <input type="text" name="emailFromEmail" value="${fn:escapeXml(emailFromEmail)}" />
              </td>
           </tr>
           <tr>
              <td><fmt:message key="edit_defaults.unsubscribe.emailsubject" />:</td>
              <td></td>
              <td>
                 <input type="text" name="emailSubject" value="${fn:escapeXml(emailSubject)}" />
              </td>
           </tr>
           <tr>
              <td><fmt:message key="edit_defaults.unsubscribe.emailtext" />:</td>
              <td></td>
              <td>
                 <textarea name="emailText" rows="5" cols="20"><c:out value="${emailText}" /></textarea>
              </td>
           </tr>
			<tr>
				<td colspan="3">
					<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
				</td>
			</tr>
		</table>
		${themes}
	</form>
</div>
