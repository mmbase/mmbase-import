<mm:node number="${param.elementId}">
	<cmsc:image/>
</mm:node>

<cmsc:location var="currentPage"/>

<form name="<portlet:namespace />form" method="post" id="<portlet:namespace />form" action="<cmsc:actionURL/>" >
	<input type="hidden" name="elementId" value="${param.elementId}"/>
	<input type="hidden" name="galleryId" value="${param.selgallery}"/>
	<input type="hidden" name="pageId" value="${currentPage.id}"/>
	<input type="hidden" name="ecardWindow" value="${ecardWindow}"/>
	
	<br>
	<fmt:message key="view.ecard.from"/>			
	<br>
	<fmt:message key="view.ecard.fromName"/><input type="text" name="fromName" value="${fromName}"/>
	<c:if test="${!empty errormessages['fromName']}">
		<span class="smallerror">	
			<br/><fmt:message key="${errormessages['fromName']}" />
		</span> 
	</c:if>	
	<br>
	<fmt:message key="view.ecard.fromEmail"/><input type="text" name="fromEmail" value="${fromEmail}"/>
	<c:if test="${!empty errormessages['fromEmail']}">
		<span class="smallerror">	
			<br/><fmt:message key="${errormessages['fromEmail']}" />
		</span> 
	</c:if>
	<br>
	<fmt:message key="view.ecard.to"/>
	<br>
	<fmt:message key="view.ecard.toName"/><input type="text" name="toName" value="${toName}"/>
	<c:if test="${!empty errormessages['toName']}">
		<span class="smallerror">	
			<br/><fmt:message key="${errormessages['toName']}" />
		</span> 
	</c:if>
	<br>
	<fmt:message key="view.ecard.toEmail"/><input type="text" name="toEmail" value="${toEmail}"/>
	<c:if test="${!empty errormessages['toEmail']}">
		<span class="smallerror">	
			<br/><fmt:message key="${errormessages['toEmail']}" />
		</span> 
	</c:if>
	<br>
	<fmt:message key="view.ecard.textBody"/><textarea name="textBody">${textBody}</textarea>
	<c:if test="${!empty errormessages['textBody']}">
		<span class="smallerror">	
			<br/><fmt:message key="${errormessages['textBody']}" />
		</span> 
	</c:if>
	<c:if test="${!empty sendNewsletter}">
		<c:set var="checkedval" value="CHECKED"/>
	</c:if>
	<br>
	<fmt:message key="view.ecard.sendNewsletter"/><input type="checkbox" name="sendNewsletter" ${checkedval} value="yes"/>
	<br>
	<input type="submit" value="<fmt:message key="view.submit"/>"/>

</form>
<a href="${funpage}"><fmt:message key="view.ecard.terug"/></a>	