<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

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

<div class="portlet-config-canvas">
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">

			<%-- Portletdefinition display --%>
			<c:import url="../sections/definitiondisplay.jsp" />

			<tr>
				<td colspan="3"><h4><fmt:message key="edit_defaults.register.group" /></h4></td>
			</tr>
			<tr>
				<td colspan="2" ><fmt:message key="edit_defaults.register.defaultgroup" />:</td>
				<td>
					<cmsc:select var="groupName">
						<cmsc:option value="nogroup" message="edit_defaults.register.nogroup" />
						<c:forEach items="${allGroupNames}" var="group">
						<cmsc:option value="${group}">${group}</cmsc:option>
						</c:forEach>
					</cmsc:select>
				</td>
			</tr>
			<tr>
				<td colspan="3"><h4><fmt:message key="edit_defaults.register.subject" /></h4></td>
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
					<textarea name="emailText" rows="5" cols="20"><c:out value="${emailText}" /></textarea>
				</td>
			</tr>
			<tr> 
            <td colspan="3"><h4><fmt:message key="edit_defaults.register.terms" /></h4></td>
         </tr>
          <tr>
            <td><fmt:message key="edit_defaults.register.useterms" />:</td>
            <td></td>
            <td>
               <select name="useterms">
                  <option ${(useterms eq 'yes')?'selected':''} value="yes"><fmt:message key="edit_defaults.yes"/></option>
                  <option ${(useterms eq 'yes')?'':'selected'} value="no"><fmt:message key="edit_defaults.no"/></option>
               </select>
            </td>
         </tr>
         <tr>
            <td><fmt:message key="edit_defaults.register.termspage" />:</td>
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

			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />

		</table>
	</form>
</div>