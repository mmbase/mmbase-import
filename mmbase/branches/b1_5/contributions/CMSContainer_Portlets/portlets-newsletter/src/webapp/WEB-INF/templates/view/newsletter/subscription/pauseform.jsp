<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<fmt:setBundle basename="portlets-newslettersubscription" scope="request"/>
<form method="post" name="<portlet:namespace />form_subscribe"
      action="<cmsc:actionURL><cmsc:param name="action" value="pause"/></cmsc:actionURL>"
      >
   <div class="content">
      <c:choose>
         <c:when test="${fn:length(subscriptionsToBePause) gt 0}">
            <div><fmt:message key="subscription.subscribe.operation.confirmterminate"/></div>
            <ul>
               <c:forEach items="${subscriptionsToBePause}" var="subscription">
                  <li>${subscription.newsletter.title}</li>
                  <input type="checkbox" name="subscriptions" value="${subscription.id}" checked="checked"
                         style="visibility:hidden;"/>
               </c:forEach>
            </ul>
         </c:when>
         <c:otherwise>
            <fmt:message key="subscription.subscribe.status.nopausedsubscription"/>
         </c:otherwise>
      </c:choose>
      <fieldset>
         <legend><fmt:message key="subscription.subscribe.operation.pause.form.title"/></legend>
         <div>
            <label for="timeduration"><fmt:message
                  key="subscription.subscribe.operation.pause.form.timeduration"/></label>
            <input type="text" name="timeduration" id="timeduration">
            <select name="durationunit" id="durationunit">
               <option value="d"><fmt:message key="subscription.subscribe.operation.pause.form.day"/></option>
               <option value="w"><fmt:message key="subscription.subscribe.operation.pause.form.week"/></option>
               <option value="m"><fmt:message key="subscription.subscribe.operation.pause.form.month"/></option>
            </select>
         </div>
         <div>
            <label for="resumeDate"><fmt:message key="subscription.subscribe.operation.pause.form.resumeDate"/></label>
            <input type="text" name="resumeDate" id="resumeDate">
         </div>

      </fieldset>
       <input type="hidden" name="confirm_pause" value="confirm_pause"/>
      <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
         <fmt:message key="subscription.subscribe.operation.pause"/>
      </a>
   </div>
</form>