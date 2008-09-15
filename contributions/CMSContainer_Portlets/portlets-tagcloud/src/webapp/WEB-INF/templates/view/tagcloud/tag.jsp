<%@include file="../../portletglobals.jsp"%>

<%--

This view is meant to serve as example for a view which can be used in the TagPortlet

--%>

Tag: ${tag}<br/><br/>
<c:if test="${!empty tag}">
	<mm:cloud>
		<mm:list path="tag,contentelement" constraints="tag.name = '${tag}'">
			<c:set var="number"><mm:field name="contentelement.number"/></c:set>
			<c:if test="${!empty page}">
			    <cmsc:renderURL page="${page}" window="${window}" var="renderUrl">
			        <cmsc:param name="tag" value="${tag}" />
			        <cmsc:param name="elementId" value="${number}"/>
			    </cmsc:renderURL>
			    <a href="${renderUrl}">
		    </c:if>
   			[<mm:field name="contentelement.number"/>] <mm:field name="contentelement.title"/><br/>			
			<c:if test="${!empty page}">
				</a>
			</c:if>
		</mm:list>
	</mm:cloud>
</c:if>
