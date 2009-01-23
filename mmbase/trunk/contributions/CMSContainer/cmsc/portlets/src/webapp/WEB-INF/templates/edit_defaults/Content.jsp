<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<div class="portlet-config-canvas">
	<h3><fmt:message key="edit_defaults.title" /></h3>
	<form name="<portlet:namespace />form" method="post" target="_parent" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table class="editcontent">
			<tr>
				<td colspan="3" align="center"><h4><fmt:message key="edit_defaults.portletset"/></h4></td>
			</tr>

			<%-- Portletdefinition display --%>
			<c:import url="sections/definitiondisplay.jsp" />
		
			<%-- View selector --%>
			<c:import url="sections/viewselector.jsp" />
		
			<%-- Content element selector --%>
			<tr>
				<td colspan="3"><h4><fmt:message key="edit_defaults.contentset"/></h4></td>
			</tr>
			<tr>
				<td><fmt:message key="edit_defaults.contentelement" />:</td>
				<td align="right">
					<a href="<c:url value='/editors/repository/select/index.jsp?contentnumber=${contentelement}' />"
						target="selectcontentelement" onclick="openPopupWindow('selectcontentelement', 900, 400)"> 
							<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.contentselect" />"/></a>
					<a href="javascript:erase('contentelement');erase('contentelementtitle')">
						<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/></a>
				</td>
				<td>
				<mm:cloud>
					<mm:node number="${contentelement}" notfound="skip">
						<mm:field name="title" id="contentelementtitle" write="false" />
					</mm:node>
				</mm:cloud>
				<input type="hidden" name="contentelement" value="${contentelement}" />
				<input type="text" name="contentelementtitle" value="${contentelementtitle}" disabled="true" />
			</tr>
			
			<%-- Use lifecycle option--%>
			<c:import url="sections/lifecycle.jsp" />
			
			<%-- Click to page options --%>
			<c:import url="sections/clicktopage.jsp" />
		
			<%-- Save button --%>
			<c:import url="sections/savebutton.jsp" />
			
		</table>
	</form>
</div>