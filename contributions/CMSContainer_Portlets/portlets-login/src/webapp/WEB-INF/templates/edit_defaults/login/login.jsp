<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>
<div class="portlet-config-canvas">
<script type="text/javascript">
   function selectPage(page, path, positions) {
      document.forms['<portlet:namespace />form'].page.value = page;
      document.forms['<portlet:namespace />form'].pagepath.value = path;
   }
   function erase(field) {
      document.forms['<portlet:namespace />form'][field].value = '';
   }
</script>
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="../sections/definitiondisplay.jsp" />
		
			<tr>
				<td colspan="3"><h4><fmt:message key="view.account.sendpassword" /></h4></td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailfromname" />:</td>
				<td>
					<input type="text" name="emailFromName" value="${fn:escapeXml(emailFromName)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailfromemail" />:</td>
				<td>
					<input type="text" name="emailFromEmail" value="${fn:escapeXml(emailFromEmail)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailsubject" />:</td>
				<td>
					<input type="text" name="emailSubject" value="${fn:escapeXml(emailSubject)}" />
				</td>
			</tr>
			<tr>
				<td colspan="2"><fmt:message key="edit_defaults.register.emailtext" />:</td>
				<td>
					<textarea name="emailText" rows="8" cols="25"><c:out value="${emailText}" /></textarea>
				</td>
			</tr>
        <tr>
           <td nowrap><fmt:message key="edit_defaults.login.redirect" />:</td>
           <td nowrap> 
               <a href="<c:url value='/editors/site/select/SelectorPage.do?channel=${page}' />"
                  target="selectpage" onclick="openPopupWindow('selectpage', 340, 400)"> 
                     <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/></a>
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
			<%-- Save button --%>
			<c:import url="../sections/savebutton.jsp" />
			
		</table>
	</form>
</div>