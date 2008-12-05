<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>

<fmt:setBundle basename="portlets-newslettersubscription" scope="request"/>
<form method="POST" name="<portlet:namespace />form_subscribe" action="<cmsc:actionURL/>">
   <div class="content">
      <c:choose>
      <c:when test="${fn:length(subscriptionsToBeResume) gt 0}">
      <div><fmt:message key="subscription.subscribe.operation.confirmterminate"/></div>
      <ul>
         <c:forEach items="${subscriptionsToBeResume}" var="subscription">
            <li>${subscription.newsletter.title}</li>
            <input type="checkbox" name="subscriptions" value="${subscription.id}" checked="checked"
                   style="visibility:hidden;"/>
         </c:forEach>
      </ul>
      <input type="hidden" name="action" value="resume"/>
      <input type="hidden" name="confirm_resume" value="confirm_resume"/>

      <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
         <fmt:message key="subscription.subscribe.operation.resume"/>
      </a>
   </div>
   </c:when>
   <c:otherwise>
      <fmt:message key="subscription.subscribe.status.nopausedsubscription"/>
   </c:otherwise>
   </c:choose>
</form>



