<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<fmt:setBundle basename="portlets-newslettersubscription" scope="request"/>

<form method="POST" name="<portlet:namespace />form_subscribe" action="<cmsc:renderURL/>" target="_self">
   <input name="action" type="hidden" value="subscribe"/>

   <div class="heading">
      <h3><fmt:message key="subscription.introduction.title"/></h3>
   </div>
   <div class="content">
      <p><fmt:message key="subscription.introduction.info"/>
   </div>
   <c:choose>
      <c:when test="${isUserLogin}">
         <p><fmt:message key="subscription.subscribe.info"/></p>
         <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
            <img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/>
            <fmt:message key="subscription.introduction.buttontext"/>
         </a>
      </c:when>
      <c:otherwise>
         <fmt:message key="subscription.login.info"/>
      </c:otherwise>
   </c:choose>
</form>