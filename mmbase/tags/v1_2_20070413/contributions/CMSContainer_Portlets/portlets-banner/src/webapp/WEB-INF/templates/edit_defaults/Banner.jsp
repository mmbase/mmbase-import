<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">
<script type="text/javascript">

function selectChannel(channel, path) {
	document.forms['<portlet:namespace />form'].contentchannel.value = channel;
	document.forms['<portlet:namespace />form'].contentchannelpath.value = path;
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

var repositoryUrl = "<cmsc:staticurl page='/editors/repository/index.jsp'/>";
function openRepositoryWithChannel() {
	contentchannel = document.forms['<portlet:namespace />form'].contentchannel.value;
	if(contentchannel == undefined || contentchannel == '') {
		alert('<fmt:message key="edit_defaults.preview.noChannel"/>');
	}
	else {
		if(confirm('<fmt:message key="edit_defaults.preview.loseChanges"/>')) {
			window.top.bottompane.location = repositoryUrl + '?channel=' + contentchannel;
		}
	}
}
</script>

<form name="<portlet:namespace />form" method="post" target="_parent"
    action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

<table class="editcontent">
	<tr>
		<td colspan="3">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<h3><fmt:message key="edit_defaults.title" /></h3>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="edit_defaults.channel" />:</td>
		<td align="right">
			<a href="javascript:openRepositoryWithChannel()">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/preview.png'/>" alt="<fmt:message key="edit_defaults.preview"/>"/></a>
			<a href="<c:url value='/editors/repository/select/SelectorChannel.do?channel=${contentchannel}' />"
				target="selectchannel" onclick="openPopupWindow('selectchannel', 340, 400)"> 
					<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/></a>
			<a href="javascript:erase('contentchannel');erase('contentchannelpath')">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/></a>
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
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.view" />:</td>
		<td><cmsc:select var="view">
			<c:forEach var="v" items="${views}">
				<cmsc:option value="${v.id}" name="${v.title}" />
			</c:forEach>
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="3">
			<h4><fmt:message key="edit_defaults.content" /></h4>
		</td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.uselifecycle" />:</td>
		<td><cmsc:select var="useLifecycle">
			<cmsc:option value="true" message="edit_defaults.yes" />
			<cmsc:option value="false" message="edit_defaults.no" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.archive" />:</td>
		<td><cmsc:select var="archive">
			<cmsc:option value="all" message="edit_defaults.archive.all" />
			<cmsc:option value="new" message="edit_defaults.archive.new" />
			<cmsc:option value="old" message="edit_defaults.archive.old" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.orderby" />:</td>
		<td><cmsc:select var="orderby">
			<cmsc:option value="" message="edit_defaults.orderby.channelposition" />
			<cmsc:option value="title" message="edit_defaults.orderby.title" />
			<cmsc:option value="description" message="edit_defaults.orderby.description" />
			<cmsc:option value="creationdate" message="edit_defaults.orderby.creationdate" />
			<cmsc:option value="lastmodifieddate" message="edit_defaults.orderby.lastmodifieddate" />
			<cmsc:option value="publishdate" message="edit_defaults.orderby.publishdate" />
			<cmsc:option value="expirydate" message="edit_defaults.orderby.expirydate" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.direction" />:</td>
		<td><cmsc:select var="direction">
            <cmsc:option value="DOWN" message="edit_defaults.descending" />
			<cmsc:option value="UP" message="edit_defaults.ascending" />
		</cmsc:select></td>
	</tr>
	<tr>
		<td colspan="2"><fmt:message key="edit_defaults.maxelements" />:</td>
		<td>
			<input type="text" name="maxElements" value="${maxElements}" />
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
		</td>
	</tr>
</table>
</form>
</div>