<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<c:if test="${not empty param.elementId}">
    <c:set var="elementId" value="${param.elementId}" scope="request"/>
</c:if>

<c:if test="${not empty elementId}">
	<c:if test="${not empty elementHeader}">
		<c:import url="${elementHeader}"/>
	</c:if>
    <c:import url="${elementTemplate}"/>
	<c:if test="${not empty elementFooter}">
		<c:import url="${elementFooter}"/>
	</c:if>
</c:if>

<c:if test="${empty elementId}">
	<c:if test="${empty pagerIndex}">
		<c:set var="pagerIndex" value="/WEB-INF/templates/pagerindex.jsp" />
	</c:if>

	<cmsc:pager maxPageItems="${elementsPerPage}" items="${totalElements}" 
			index="${pagesIndex}" maxIndexPages="${showPages}" isOffset="true"
			export="offset,currentPage=pageNumber">
	<c:if test="${usePaging}">
		<c:set var="startPage" value="${offset + 1}" scope="request"/>
		<c:set var="endPage" value="${startPage + (elementsPerPage - 1)}" scope="request"/>
		<c:if test="${endPage > totalElements}">
			<c:set var="endPage" value="${totalElements}" scope="request"/>
		</c:if>
		<c:if test="${position == 'top' || position == 'both'}">
			<c:import url="${pagerIndex}"/>
		</c:if>
	</c:if>
   <c:choose>
      <c:when test="${fn:toLowerCase(direction) == 'up'}">
         <c:set var="offset" value="${offset}" scope="request"/>
      </c:when>
      <c:otherwise>
         <c:set var="offset" value="${totalElements - endPage}" scope="request"/>
      </c:otherwise>
   </c:choose>
   <c:if test="${not empty listHeader}">
	    <c:import url="${listHeader}"/>
	</c:if>
   <c:if test="${empty elements && not empty newsletterNoContent}">
       <c:import url="${newsletterNoContent}"/>
   </c:if>
	<c:forEach var="elem" items="${elements}" varStatus="listStatus">
	<pg:item>
	   	<c:set var="elementIndex" value="${listStatus.index}" scope="request"/>
	   	<c:set var="elementCount" value="${listStatus.count}" scope="request"/>
	   	<c:set var="elementFirst" value="${listStatus.first}" scope="request"/>
	   	<c:set var="elementLast" value="${listStatus.last}" scope="request"/>
	   	<c:set var="elementId" value="${elem.id}" scope="request"/>
	   	<c:set var="elementTitle" value="${elem.title}" scope="request"/>
		<c:if test="${not empty itemHeader}">
			<c:import url="${itemHeader}"/>
		</c:if>
		<c:choose>
			<c:when test="${displaytype eq 'detail'}">
			    <c:import url="${elementTemplate}"/>
			</c:when>
			<c:otherwise>
			    <c:import url="${itemTemplate}"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty itemFooter}">
			<c:import url="${itemFooter}"/>
		</c:if>
	   	<c:remove var="elementIndex" scope="request"/>
	   	<c:remove var="elementCount" scope="request"/>
	   	<c:remove var="elementFirst" scope="request"/>
	   	<c:remove var="elementLast" scope="request"/>
	   	<c:remove var="elementId" scope="request"/>
	   	<c:remove var="elementTitle" scope="request"/>
	</pg:item>
	</c:forEach>
	<c:if test="${not empty listFooter}">
	    <c:import url="${listFooter}"/>
	</c:if>
	<c:if test="${usePaging}">
		<c:if test="${position == 'bottom' || position == 'both'}">	
			<c:import url="${pagerIndex}"/>
		</c:if>
		<c:remove var="offset" scope="request"/>
		<c:remove var="startPage" scope="request"/>  
		<c:remove var="endPage" scope="request"/>
	</c:if>
	</cmsc:pager>
</c:if>