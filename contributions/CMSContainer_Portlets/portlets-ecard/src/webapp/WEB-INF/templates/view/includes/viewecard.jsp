<mm:node number="${param.galleryId}" notfound="skip">
		<mm:field name="title" id="galleryname" write="false"/>
</mm:node>
<mm:node number="${param.ecardId}" notfound="skip">
	<c:if test="${param.mailkey eq _node.mailkey}">
		<mm:relatednodes type="images" role="posrel" searchdir="destination">
			<mm:field name="title" id="imagename" write="false"/>
			<mm:field name="number" id="imagenumber" write="false"/>
		</mm:relatednodes>
		<br>
		${galleryname}: ${imagename} <br>
		
		<mm:node referid="imagenumber">
			<cmsc:image/>		
		</mm:node>
		<br>
		<fmt:message key="view.ecard.from"/>			
		<br>
		<fmt:message key="view.ecard.fromName"/>  <mm:field name="fromname" escape="text/xml"/> <br>
		<fmt:message key="view.ecard.fromEmail"/> <mm:field name="fromemail"/> <br>
		<fmt:message key="view.ecard.toName"/> <mm:field name="toname" escape="text/xml"/> <br>
		<fmt:message key="view.ecard.toEmail"/> <mm:field name="toemail"/> <br>
		<fmt:message key="view.ecard.textBody"/> <mm:field name="body" escape="text/xml"/> <br>
	</c:if>		
</mm:node>
<cmsc:renderURL var="viewfunpage">
	<cmsc:param name="selgallery" value="${param.galleryId}"/>
</cmsc:renderURL>
<a href="${viewfunpage}"><fmt:message key="view.ecard.terug"/></a>	