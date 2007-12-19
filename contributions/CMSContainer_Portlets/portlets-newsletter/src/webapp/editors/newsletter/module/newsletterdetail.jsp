<%@include file="globals.jsp" %>

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><fmt:message key="newsletterdetail.title" />: ${newsletterDetailBean.title}</b>

<table width="50%">
	<tr>
		<th align="left"><fmt:message key="newsletterdetail.username" /></tk>
		<th align="left"><fmt:message key="newsletterdetail.numberofthemes" /></tk>
	</tr>
	<c:forEach var="bean" items="${newsletterDetailBean.subscribers}">
	<c:url var="url" value="SubscriberAction.do?action=detail&username=${bean.userName}" />
	<tr>
		<td><a href="${url}"><jsp:getProperty name="bean" property="userName" /></a></td>
		<td><jsp:getProperty name="bean" property="numberOfThemes" /></td>
	</tr>
	</c:forEach>
</table>