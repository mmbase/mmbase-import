<jsp:useBean id="newsletterBean" class="com.finalist.newsletter.module.NewsletterBean"scope="session" />
<jsp:setProperty name="newsletterBean" property="number" value="${number}" />

<fmt:setBundle basename="newsletter-module" scope="request" />

<b><td><fmt:message key="module.newsletterdetail.title" /></td></b>
<table width="50%">

</table>