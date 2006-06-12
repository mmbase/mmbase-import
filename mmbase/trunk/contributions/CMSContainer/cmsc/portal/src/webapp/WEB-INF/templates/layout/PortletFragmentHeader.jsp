<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/csmc" prefix="cmsc" %>
<cmsc:protected>
	<fmt:setBundle basename="cmsc-portal" scope="request" />

<cmsc:portlet infovar="portletinfo">
<c:choose>
	<c:when test="${portletinfo.currentMode.type == 'view'}">
		<div class="portlet-canvas" id="portlet-${portletinfo.id}">
<%-- 
			onmouseover="showMode(event, 'portlet-${portletinfo.id}', 'portlet-header-${portletinfo.id}')"
			onmouseout="hideMode(event, 'portlet-${portletinfo.id}', 'portlet-header-${portletinfo.id}')"
--%>

			<div class="portlet-header-canvas" id="portlet-header-${portletinfo.id}" > <%--  style="display: none;" --%>
	</c:when>
	<c:otherwise>
		<div class="portlet-canvas" id="portlet-${portletinfo.id}">
			<div class="portlet-header-canvas" id="portlet-header-${portletinfo.id}">
	</c:otherwise>
</c:choose>
<div class="portlet-mode-canvas">
		<c:forEach items="${portletinfo.visiblePortletModes}" var="modeInfo" >
			<a href="${modeInfo.url}" title="<fmt:message key='portletmode.${modeInfo.name}' />" 
				class="portlet-mode-type-${modeInfo.type}">
				<img src="<cmsc:staticurl page='/icons/${modeInfo.name}.gif'/>" border="0"  
					alt="<fmt:message key='portletmode.${modeInfo.name}' />" />
			</a>
		</c:forEach>
</div>
</div>
</cmsc:portlet>
</cmsc:protected>