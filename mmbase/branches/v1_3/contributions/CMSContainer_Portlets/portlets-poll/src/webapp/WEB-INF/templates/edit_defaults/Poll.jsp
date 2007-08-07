<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas"><script type="text/javascript">
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
</script>
<form name="<portlet:namespace />form" method="post" target="_parent"
   action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
<table class="editcontent">
   <tr>
      <td colspan="3"><a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
      <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt="" /> <fmt:message key="edit_defaults.save" /></a>
      </td>
   </tr>
   <tr>
      <td colspan="3">
      <h3><fmt:message key="edit_defaults.title" /></h3>
      </td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.contentelement" />:</td>
      <td><a href="<c:url value='/editors/repository/select/index.jsp?contentnumber=${contentelement}' />"
         target="selectcontentelement" onclick="openPopupWindow('selectcontentelement', 900, 400)"> <img
         src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>"
         alt="<fmt:message key="edit_defaults.contentselect" />" /> </a></td>
      <td><mm:cloud>
         <mm:node number="${contentelement}" notfound="skip">
            <mm:field name="title" id="contentelementtitle" write="false" />
         </mm:node>
      </mm:cloud> <input type="hidden" name="contentelement" value="${contentelement}" /> <input type="text"
         name="contentelementtitle" value="${contentelementtitle}" disabled="true" /></td>
   </tr>
   <tr>
      <td colspan="2"><fmt:message key="edit_defaults.view" />:</td>
      <td><cmsc:select var="view">
         <c:forEach var="v" items="${views}">
            <cmsc:option value="${v.id}" name="${v.title}" />
         </c:forEach>
      </cmsc:select></td>
   </tr>
   <tr>
      <td colspan="3"><a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
      <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt="" /> <fmt:message key="edit_defaults.save" /></a>
      </td>
   </tr>
</table>
</form>
</div>
