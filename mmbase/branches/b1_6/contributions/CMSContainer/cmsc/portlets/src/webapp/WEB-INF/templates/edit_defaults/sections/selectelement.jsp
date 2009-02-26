<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="3"><h4><fmt:message key="edit_defaults.contentset"/></h4></td>
</tr>
<tr>
	<td><fmt:message key="edit_defaults.contentelement" />:</td>
	<td align="right">
		<a href="<c:url value='/editors/repository/select/index.jsp?contentnumber=${contentelement}' />" target="selectcontentelement" onclick="openPopupWindow('selectcontentelement', 900, 400)"> 
			<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.contentselect" />"/>
		</a>
		<a href="javascript:erase('contentelement');erase('contentelementtitle')">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
		</a>
	</td>
	<td>
		<mm:cloud>
			<mm:node number="${contentelement}" notfound="skip">
				<mm:field name="title" id="contentelementtitle" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="contentelement" value="${contentelement}" />
		<input type="text" name="contentelementtitle" value="${contentelementtitle}" disabled="true" />
	</td>
</tr>