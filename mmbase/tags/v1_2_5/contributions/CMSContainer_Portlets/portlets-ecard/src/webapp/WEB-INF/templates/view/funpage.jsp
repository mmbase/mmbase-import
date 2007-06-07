<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:cloud method="asis">
<fmt:message key="view.ecard.title"/> <br>
<c:choose>
	<c:when test="${not empty param.ecardId && not empty param.mailkey}">
		<%@include file="includes/viewecard.jsp" %>
	</c:when>
	<c:when test="${empty param.elementId}">    
		<%@include file="includes/list.jsp" %>
	</c:when>
	<c:otherwise>
		<mm:node number="${param.selgallery}" notfound="skip">
			<mm:field name="title" id="galleryname" write="false"/>
		</mm:node>
		<mm:node number="${param.elementId}" notfound="skip">
			<mm:field name="title" id="imagename" write="false"/>
		</mm:node>
		<br>
		${galleryname}: ${imagename} <br>	
		<cmsc:renderURL var="funpage">
			<cmsc:param name="selgallery" value="${param.selgallery}"/>
		</cmsc:renderURL>
		<c:choose>
			<c:when test="${empty param.emailsent}">
				<%@include file="includes/ecardform.jsp" %>	
			</c:when>
			<c:otherwise>
				<%@include file="includes/confirm.jsp" %>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
</mm:cloud>