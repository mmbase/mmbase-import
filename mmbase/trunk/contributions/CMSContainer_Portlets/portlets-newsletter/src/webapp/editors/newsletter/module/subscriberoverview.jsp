<%@include file="globals.jsp" %>

<fmt:setBundle basename="subscriber-module" scope="request" />

<b><fmt:message key="subscriberoverview.title" /></b>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="subscriberoverview.username" /></tk>
		<th align="left"><fmt:message key="subscriberoverview.status" /></tk>
		<th align="left"><fmt:message key="subscriberoverview.mimetype" /></tk>
		<th align="left"><fmt:message key="subscriberoverview.numberofnewsletters" /></tk>
		<th align="left"><fmt:message key="subscriberoverview.numberofthemes" /></tk>
	</tr>
	<c:forEach var="bean" items="${subscriberOverviewBeans}">
	<c:url var="url" value="SubscriberAction.do?action=detail&name=${bean.userName}" />
	<tr>
		<td><a href="${url}"><jsp:getProperty name="bean" property="userName" /></a></td>
		<td><jsp:getProperty name="bean" property="status" /></td>
		<td><jsp:getProperty name="bean" property="mimeType" /></td>
		<td><jsp:getProperty name="bean" property="numberOfNewsletters" /></td>
		<td><jsp:getProperty name="bean" property="numberOfThemes" /></td>
	</tr>
	</c:forEach>
</table>