<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="newsletter_introduction">
   <h3><fmt:message key="subscription.introduction.title"/></h3>
  
   <p><fmt:message key="subscription.introduction.info"/></p>
   
   <c:choose>
      <c:when test="${isUserLogin}">
         <form method="post" name="<portlet:namespace />form_subscribe" action="<cmsc:renderURL/>">
            <input name="action" type="hidden" value="subscribe"/>
            <p><fmt:message key="subscription.subscribe.info"/></p>
            <input type="submit" value="<fmt:message key="subscription.introduction.buttontext" />" >
         </form>
      </c:when>
      <c:otherwise>
         <p><fmt:message key="subscription.login.info"/></p>
      </c:otherwise>
   </c:choose>

</div>