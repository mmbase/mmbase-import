<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:if test="${empty active}">
   <h3><fmt:message key="register.newsletter.success" /></h3>
   <p><fmt:message key="register.newsletter.success.information" /></p>
</c:if>
<c:if test="${!empty active}">
   <p>
   <c:if test="${active == 'success'}">
      <fmt:message key="register.newsletter.active.success" />
   </c:if>
   <c:if test="${active == 'failure'}">
      <fmt:message key="register.newsletter.active.failure" />
   </c:if>
   <c:if test="${active == 'actived'}">
      <fmt:message key="register.newsletter.active.alreadyactivated" />
   </c:if>
   </p>
</c:if>