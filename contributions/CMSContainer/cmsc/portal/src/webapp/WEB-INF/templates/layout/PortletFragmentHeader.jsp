<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<cmsc:protected>
	<fmt:setBundle basename="cmsc-portal" scope="request" />

<cmsc:portlet infovar="portletInfo">
<c:choose>
	<c:when test="${portletInfo.id == -1}">
		<div class="portlet-canvas">
			<div class="portlet-header-canvas">
	</c:when>
	<c:otherwise>
		<div class="portlet-canvas" id="portlet-${portletInfo.id}">
			<div class="portlet-header-canvas" id="portlet-header-${portletInfo.id}">
	</c:otherwise>
</c:choose> 
<div class="portlet-mode-canvas portlet-mode-type-${portletInfo.currentMode.name}">

${requestScope.fragment.key}

<c:forEach items="${portletInfo.visiblePortletModes}" var="modeInfo" >
	<a href="${modeInfo.url}" title="<fmt:message key='portletmode.${modeInfo.name}' />" class="portlet-mode-type-${modeInfo.type}">
		<img src="<cmsc:staticurl page='/editors/gfx/icons/${modeInfo.name}.png'/>" border="0" alt="<fmt:message key='portletmode.${modeInfo.name}' />"/></a>
</c:forEach>
</div>
<br/>
</div>
<div class="portlet-mode-spacer"></div>
</cmsc:portlet>
</cmsc:protected>