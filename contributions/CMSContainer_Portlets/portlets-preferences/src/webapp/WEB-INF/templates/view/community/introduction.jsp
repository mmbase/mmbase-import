<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<form method="POST" name="<portlet:namespace />form_preference" action="<cmsc:renderURL/>" target="_self">
<br/>
   <div class="heading">
      <h3><fmt:message key="preference.introduction.title"/></h3>
   </div>
   <div class="content">
      <p><fmt:message key="preference.introduction.info"/>
   </div>
   <c:choose>
      <c:when test="${isUserLogin}">
         <p><fmt:message key="preference.subscribe.info"/></p>
         <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
            <img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/>
            <fmt:message key="preference.introduction.buttontext"/>
         </a>
      </c:when>
      <c:otherwise>
         <fmt:message key="preference.login.info"/>
      </c:otherwise>
   </c:choose>
</form>
