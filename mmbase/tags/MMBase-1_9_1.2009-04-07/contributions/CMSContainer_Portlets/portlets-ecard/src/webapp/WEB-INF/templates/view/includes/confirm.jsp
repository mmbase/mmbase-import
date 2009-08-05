<c:out value="${confirmation}"/> <br>			
<mm:node number="${param.ecardId}" notfound="skip">
	<fmt:message key="view.ecard.sentTo"/> <mm:field name="toemail" escape="text/xml"/> <br> 
	<fmt:message key="view.ecard.sentText"/> <mm:field name="body" escape="text/xml"/>
	<fmt:message key="view.ecard.sendagain"/> <a href="${funpage}"><fmt:message key="view.ecard.here"/></a>
</mm:node>