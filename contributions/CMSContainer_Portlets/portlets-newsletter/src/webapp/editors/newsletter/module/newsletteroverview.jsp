<%@include file="globals.jsp" %>

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><fmt:message key="module.newsletterdetail.title" /></b>

<table width="50%">
	<c:forEach var="beanName" items="newsletterOverviewBeans">
	<jsp:useBean id="${beanName}" scope="request" type="com.finalist.newsletter.module.bean.NewsletterOverviewBean" />
	<tr>

	</tr>
	</c:forEach>
</table>