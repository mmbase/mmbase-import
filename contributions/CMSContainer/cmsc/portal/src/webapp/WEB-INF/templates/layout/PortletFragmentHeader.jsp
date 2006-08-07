<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://finalist.com/csmc" prefix="cmsc" %>
<cmsc:protected>
	<fmt:setBundle basename="cmsc-portal" scope="request" />

<cmsc:portlet infovar="portletinfo">
<c:choose>
	<c:when test="${portletinfo.id == -1}">
		<div class="portlet-canvas">
			<div class="portlet-header-canvas">
	</c:when>
	<c:otherwise>
		<div class="portlet-canvas" id="portlet-${portletinfo.id}">
			<div class="portlet-header-canvas" id="portlet-header-${portletinfo.id}" >
	</c:otherwise>
</c:choose> 
<div class="portlet-mode-canvas">

${requestScope.layoutId}

		<c:forEach items="${portletinfo.visiblePortletModes}" var="modeInfo" >
			<a href="${modeInfo.url}" title="<fmt:message key='portletmode.${modeInfo.name}' />" 
				class="portlet-mode-type-${modeInfo.type}">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/${modeInfo.name}.png'/>" border="0"  
					alt="<fmt:message key='portletmode.${modeInfo.name}' />" />
			</a>
		</c:forEach>
</div>
</div>
<div style="height:22px"></div>
</cmsc:portlet>
</cmsc:protected>