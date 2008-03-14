<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<c:set var="itemHeader" value="fragments/header_banner.jsp" />
<c:set var="itemFooter" value="fragments/footer_banner.jsp" />
<c:set var="itemTemplate" value="fragments/banner.jsp" />
<c:if test="${empty param.elementId}">	    
	<c:forEach var="elem" items="${elements}" varStatus="listStatus">		
        <c:set var="elementId" value="${elem.id}" scope="request"/>
		<c:if test="${listStatus.first}">
		    <c:import url="${itemHeader}"/> 
        </c:if>
		<c:import url="${itemTemplate}"/>		        			    	
		<c:if test="${listStatus.last}">
			<c:import url="${itemFooter}"/>
		</c:if>
       	<c:remove var="elementId" scope="request"/>
	</c:forEach>
</c:if>
