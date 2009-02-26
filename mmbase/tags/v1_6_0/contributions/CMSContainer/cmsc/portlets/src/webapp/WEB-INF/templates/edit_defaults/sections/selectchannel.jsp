<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<%@include file="/WEB-INF/templates/edit_defaults/sections/globals.jsp"%>

<tr>
	<td colspan="3"><h4><fmt:message key="edit_defaults.contentset"/></h4></td>
</tr>			
<tr>
	<td><fmt:message key="edit_defaults.channel" />:</td>
	<td align="right">
		<a href="javascript:openRepositoryWithChannel()">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/preview.png'/>" alt="<fmt:message key="edit_defaults.preview"/>"/>
		</a>
		<a href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${contentchannel}' />" target="selectchannel" onclick="openPopupWindow('selectchannel', 340, 400)"> 
			<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/>
		</a>
		<a href="javascript:erase('contentchannel');erase('contentchannelpath')">
			<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/>
		</a>
	</td>
	<td>
		<mm:cloud>
			<mm:node number="${contentchannel}" notfound="skip">
				<mm:field name="path" id="contentchannelpath" write="false" />
			</mm:node>
		</mm:cloud>
		<input type="hidden" name="contentchannel" value="${contentchannel}" />
		<input type="text" name="contentchannelpath" value="${contentchannelpath}" disabled="true" />
	</td>
</tr>