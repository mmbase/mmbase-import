<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<cmsc:protected>
<c:if test="${'edit' eq sessionScope.pageMode}">
   <fmt:setBundle basename="cmsc-portal" scope="request"/>

   <cmsc:portlet infovar="portletInfo">
      <c:choose>
         <c:when test="${portletInfo.id == -1}">
            <div class="portlet-canvas">
            <div class="portlet-header-canvas">
         </c:when>
         <c:otherwise>
            <div class="portlet-canvas" id="portlet-${portletInfo.id}">
            <div class="portlet-header-canvas" id="portlet-header-${portletInfo.id}"
            onmouseover="showInfo('${portletInfo.id}');" onmouseout="hideInfo('${portletInfo.id}');">
         </c:otherwise>
      </c:choose>

<div class="portlet-mode-canvas portlet-mode-type-${portletInfo.currentMode.name}" id="portlet-mode-${portletInfo.id}">

${requestScope.fragment.key}

<c:forEach items="${portletInfo.visiblePortletModes}" var="modeInfo" >
	<a href="${modeInfo.url}" title="<fmt:message key='portletmode.${modeInfo.name}' />" class="portlet-mode-type-${modeInfo.type}">
		<img src="<cmsc:staticurl page='/editors/gfx/icons/${modeInfo.name}.png'/>" border="0" alt="<fmt:message key='portletmode.${modeInfo.name}' />"/></a>
</c:forEach>

<div class="portlet-info" id="portlet-info-${portletInfo.id}">
<c:if test="${portletInfo.definitionId gt 0}">
	<fmt:message key="portletinfo.definition"/>: ${portletInfo.definitionTitle}<br/>
</c:if>
<c:if test="${portletInfo.viewId gt 0}">
	<fmt:message key="portletinfo.view"/>: ${portletInfo.viewTitle} <br/>
</c:if>
</div>
</div>
<br/>
</div>
<div class="portlet-mode-spacer"></div>
</cmsc:portlet>
   </c:if>
</cmsc:protected>
