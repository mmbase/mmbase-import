<%@include file="globals.jsp" %>

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><fmt:message key="newsletteroverview.title" /></b>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="newsletteroverview.newsletter" /></tk>
		<th align="left"><fmt:message key="newsletteroverview.numberofthemes" /></tk>
		<th align="left"><fmt:message key="newsletteroverview.numberofpublications" /></tk>
		<th align="left"><fmt:message key="newsletteroverview.numberofsubscriptions" /></tk>
	</tr>
	<c:forEach var="bean" items="${newsletterOverviewBeans}">
	<c:url var="url" value="NewsletterAction.do?action=detail&number=${bean.number}" />
	<tr>
		<td><a href="${url}"><jsp:getProperty name="bean" property="title" /></a></td>
		<td><jsp:getProperty name="bean" property="numberOfThemes" /></td>
		<td><jsp:getProperty name="bean" property="numberOfPublications" /></td>
		<td><jsp:getProperty name="bean" property="numberOfSubscriptions" /></td>
	</tr>
	</c:forEach>
</table>