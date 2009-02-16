<%@include file="taglib.tagf" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="titleClass" required="false" %>
<%@ attribute name="titleStyle" required="false" %>
<%@ attribute name="titleMode" required="false" %>
<%@ attribute name="bodyClass" required="false" %>
<%@ attribute name="bodyStyle" required="false" %>
<c:choose>
	<c:when test="${not empty titleClass}"><c:set var="titleClassInfo">class="${titleClass}"</c:set></c:when>
	<c:otherwise><c:set var="titleClassInfo">class="side_block"</c:set></c:otherwise>
</c:choose>
<c:if test="${not empty titleStyle}"><c:set var="titleStyleInfo">style="${titleStyle}"</c:set></c:if>
<c:choose>
	<c:when test="${not empty bodyClass}"><c:set var="bodyClassInfo">class="${bodyClass}"</c:set></c:when>
	<c:otherwise><c:set var="bodyClassInfo">class="body"</c:set></c:otherwise>
</c:choose>
<c:if test="${not empty bodyStyle}"><c:set var="bodyStyleInfo">style="${bodyStyle}"</c:set></c:if>
<div ${titleClassInfo} ${titleStyleInfo}>
	<div class="header">
		<div class="title"><c:choose>
			<c:when test="${not empty titleMode}"><c:out value="${title}" /></c:when>
			<c:otherwise><fmt:message key="${title}" /></c:otherwise>
			</c:choose></div>
		<div class="header_end"></div>
	</div>
	<div ${bodyClassInfo} ${bodyStyleInfo}>
		<jsp:doBody />
	</div>
	<div class="side_block_end"></div>
</div>