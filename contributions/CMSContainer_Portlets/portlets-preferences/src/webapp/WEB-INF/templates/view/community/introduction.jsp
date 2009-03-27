<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<div class="content">
   <form method="POST" name="<portlet:namespace />form_preference" action="<cmsc:renderURL/>" target="_self">
      <h3><fmt:message key="preference.introduction.title"/></h3>
         <c:choose>
            <c:when test="${isUserLogin}">
               <p>
                  <fmt:message key="preference.introduction.info"/>
                  <fmt:message key="preference.subscribe.info"/>
                  <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
                     <img src="<cmsc:staticurl page='/editors/gfx/icons/edit.png'/>" alt=""/>
                     <fmt:message key="preference.introduction.buttontext"/>
                  </a>
               </p>
            </c:when>
            <c:otherwise>
               <p><fmt:message key="preference.login.info"/></p>
            </c:otherwise>
         </c:choose>
   </form>
</div>