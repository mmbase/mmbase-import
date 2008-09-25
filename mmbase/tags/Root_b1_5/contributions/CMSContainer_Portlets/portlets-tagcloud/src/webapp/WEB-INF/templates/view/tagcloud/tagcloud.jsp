<%@include file="../../portletglobals.jsp"%>

<%--

This view is meant to serve as example for a view which can be used in the TagCloudPortlet

--%>

<c:forEach items="${tags}" var="tag">
	<c:if test="${!empty page}">
	    <cmsc:renderURL page="${page}" window="${window}" var="renderUrl">
	        <cmsc:param name="tag" value="${tag.name}" />
	    </cmsc:renderURL>
	    <a href="${renderUrl}">
    </c:if>
	<div style="font-size:${5+tag.count*3}pt; float: left;">${tag.name}&nbsp;</div>
	<c:if test="${!empty page}">
		</a>
	</c:if>
</c:forEach>
