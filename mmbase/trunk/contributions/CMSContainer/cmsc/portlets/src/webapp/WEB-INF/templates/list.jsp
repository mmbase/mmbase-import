<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<c:if test="${not empty param.elementId}">
	<c:if test="${not empty elementHeader}">
		<c:import url="${elementHeader}"/>
	</c:if>
   	<c:set var="elementId" value="${param.elementId}" scope="request"/>
    <c:import url="${elementTemplate}"/>
	<c:if test="${not empty elementFooter}">
		<c:import url="${elementFooter}"/>
	</c:if>
</c:if>

<c:if test="${empty param.elementId}">
	<portlet:renderURL var="renderUrl" />
	<pg:pager url="${renderUrl}" maxPageItems="${elementsPerPage}" items="${totalElements}" 
			index="${pagesIndex}" maxIndexPages="${showPages}" isOffset="true"
			export="offset,currentPage=pageNumber">
	<c:if test="${usePaging}">
		<c:set var="startPage" value="${offset + 1}"/>  
		<c:set var="endPage" value="${startPage + (elementsPerPage - 1)}"/>
		<c:if test="${endPage > totalElements}">
			<c:set var="endPage" value="${totalElements}"/>
		</c:if>
		<c:choose>
			<c:when test="${totalElements == 0}">
				<c:set var="pageInfo">${startPage} - ${endPage}</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="pageInfo">${startPage} - ${endPage} (${totalElements})</c:set>
			</c:otherwise>
		</c:choose>
		<p>${pageInfo}</p>
		<c:if test="${position == 'top' || position == 'both'}">	
			<%@include file="/WEB-INF/templates/pagerindex.jsp" %>
		</c:if>
	</c:if>
	<c:if test="${not empty listHeader}">
	    <c:import url="${listHeader}"/>
	</c:if>
	<c:forEach var="elem" items="${elements}">
	<pg:item>
	   	<c:set var="elementId" value="${elem.id}" scope="request"/>
		<c:if test="${not empty itemHeader}">
			<c:import url="${itemHeader}"/>
		</c:if>
		<c:choose>
			<c:when test="${viewtype eq 'detail'}">
			    <c:import url="${elementTemplate}"/>
			</c:when>
			<c:otherwise>
			    <c:import url="${itemTemplate}"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty itemFooter}">
			<c:import url="${itemFooter}"/>
		</c:if>
	</pg:item>
	</c:forEach>
	<c:if test="${not empty listFooter}">
	    <c:import url="${listFooter}"/>
	</c:if>
	<c:if test="${usePaging}">
		<c:if test="${position == 'bottom' || position == 'both'}">	
			<%@include file="/WEB-INF/templates/pagerindex.jsp" %>
		</c:if>
	</c:if>
	</pg:pager>
</c:if>