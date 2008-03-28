<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://finalist.com/cmsc-rating" prefix="cmsc-rating" %>



<%-- note: of course should this be dynamic --%>
<c:set var="userId" value="300"/>




<c:if test="${viewtype eq 'list'}">
	<cmsc:renderURL var="renderUrl" />
	<a href="${renderUrl}"><fmt:message key="view.back" /></a>
</c:if>
<mm:cloud>
	<mm:import externid="elementId" required="true" />
	<mm:node number="${elementId}" notfound="skip">
		<c:if test="${!empty param.rate}">
			<cmsc-rating:set number="${elementId}" value="${param.rate}" user="${userId}"/>
		</c:if>
		<cmsc-rating:get number="${elementId}" var="rating" countVar="count" userVar="userRating" user="${userId}"/>
		<hr/>
		Ratingtest [Has rated:${userRating != -1}|Avg rating:${rating}|Number of times rated:${count}|Our rating:${userRating}]
		<cmsc:renderURL var="rate"><cmsc:param name="rate" value="1" /></cmsc:renderURL>
		<a href="${rate}">1</a>
		<cmsc:renderURL var="rate"><cmsc:param name="rate" value="2" /></cmsc:renderURL>
		<a href="${rate}">2</a>
		<cmsc:renderURL var="rate"><cmsc:param name="rate" value="3" /></cmsc:renderURL>
		<a href="${rate}">3</a>
		<cmsc:renderURL var="rate"><cmsc:param name="rate" value="4" /></cmsc:renderURL>
		<a href="${rate}">4</a>
		<cmsc:renderURL var="rate"><cmsc:param name="rate" value="5" /></cmsc:renderURL>
		<a href="${rate}">5</a>
		<hr/>

		<mm:field name="title"><mm:isnotempty><h1><mm:write /></h1></mm:isnotempty></mm:field>
		<mm:field name="subtitle"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>

		<mm:field name="body" escape="none" />
	</mm:node>
</mm:cloud>