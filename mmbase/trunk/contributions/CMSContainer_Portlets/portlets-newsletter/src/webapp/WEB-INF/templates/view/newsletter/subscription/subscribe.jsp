<%@ page import="com.finalist.newsletter.domain.Subscription" 
%><%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<fmt:setBundle basename="portlets-newslettersubscription" scope="request"/>
<c:set var="contextPath">
   <%=request.getContextPath()%>/editors/newsletter/Subscribe.do
</c:set>
<SCRIPT LANGUAGE="JavaScript">
   function modifyStatus(newsletterId, box) {
      new Ajax.Request(
            "${contextPath}",
      {
         method: 'get',
         parameters: {newsletterId: newsletterId,select: box.checked ,action: 'modifyStatus'}
      }
            );
   }

   function addOrRemoveTag(newsletterId, termId, box) {
      new Ajax.Request(
            "${contextPath}",
      {
         method: 'get',
         parameters: {newsletterId: newsletterId, termId:termId, select: box.checked , action: 'modifyTag'}
      }
            );
   }

   function modifyFormat(newsletterId, format) {
      new Ajax.Request
            ("${contextPath}",
            {
               method: 'get',
               parameters: {newsletterId: newsletterId, format:format, action: 'modifyFormat'}
            }
                  );
   }
</SCRIPT>
<form method="POST" name="<portlet:namespace />form_subscribe"
      action="<cmsc:actionURL/>"
      >

<div class="heading">
   <h3><fmt:message key="subscription.subscribe.title"/></h3>
</div>
<div class="content">
<c:choose>
<c:when test="${fn:length(subscriptionList) > 0}">


<table border="1" width="600px">
   <tr>
      <td>&nbsp;</td>
      <td><fmt:message key="subscription.view.list.title"/></td>
      <td><fmt:message key="subscription.view.list.term"/></td>
      <td><fmt:message key="subscription.view.list.format"/></td>
      <td><fmt:message key="subscription.view.list.status"/></td>
      <td width="100px">&nbsp;</td>
   </tr>

   <c:forEach items="${subscriptionList}" var="subscription">
      <tr>
         <td>
            <c:set var="newsletterId" value="${subscription.newsletter.id}"/>
            <c:set var="status" value="${subscription.status}"/>
            <input type="checkbox"
                   value="${subscription.id}"
                   name="subscriptions"
                   id="subscription-${subscription.id}"
                   onclick="modifyStatus('${newsletterId}',this)"
               ${status ne 'INACTIVE' ? 'checked' : ''}
                  />
         </td>
         <td>
               ${subscription.newsletter.title}
         </td>
         <td>
            <%pageContext.setAttribute("terms", ((Subscription) pageContext.findAttribute("subscription")).getTerms());%>
            <c:forEach items="${terms}" var="term">
               <label for="tag-${term.id}">${term.name}</label>
               <input type="checkbox"
                      id="tag-${term.id}"
                      onclick="addOrRemoveTag('${newsletterId}','${term.id}',this)"
                  ${true eq term.subscription ? 'checked' : ''}/>
            </c:forEach>
         </td>
         <td>
            <select onchange="modifyFormat('${newsletterId}',this.value)">
               <option name="html" value="text/html" ${subscription.mimeType eq 'text/html' ? 'selected' : ''}>
                  <fmt:message key="subscription.view.list.status.html"/>
               </option>
               <option name="text" value="text/plain" ${subscription.mimeType eq 'text/plain' ? 'selected' : ''}>
                  <fmt:message key="subscription.view.list.status.text"/>
               </option>
            </select>
         </td>
         <td>
               ${subscription.status}
         </td>
         <td>
            <c:if test="${subscription.status ne 'INACTIVE'}">
               <c:set var="terminateURL">
                  <cmsc:renderURL>
                     <cmsc:param name="action" value="terminate"/>
                     <cmsc:param name="subscriptions" value="${subscription.id}"/>
                  </cmsc:renderURL>
               </c:set>
               <a href="${terminateURL}">
                  <fmt:message key="subscription.subscribe.operation.terminate"/>
               </a>

               <c:set var="pauseURL">
                  <cmsc:renderURL>
                     <cmsc:param name="action" value="pause"/>
                     <cmsc:param name="subscriptions" value="${subscription.id}"/>
                  </cmsc:renderURL>
               </c:set>
               <c:if test="${subscription.status eq 'PAUSED'}">
                  <c:set var="resumeURL">
                     <cmsc:renderURL>
                        <cmsc:param name="action" value="resume"/>
                        <cmsc:param name="subscriptions" value="${subscription.id}"/>
                     </cmsc:renderURL>
                  </c:set>
                  <fmt:message key="subscription.subscribe.status.paused"/>
                  <a href="${pauseURL}">
                     <fmt:message key="subscription.subscribe.status.paused.resumedate"/>:${subscription.resumeDate}
                  </a>
                  <a href="${resumeURL}">
                     <fmt:message key="subscription.subscribe.operation.resume"/>
                  </a>
               </c:if>
               <c:if test="${subscription.status eq 'ACTIVE'}">
                  <a href="${pauseURL}">
                     <fmt:message key="subscription.subscribe.operation.pause"/>
                  </a>
               </c:if>
            </c:if>
         </td>
      </tr>
   </c:forEach>
</table>
<br>
<input type="hidden" name="action" id="action"/>
<a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
   <fmt:message key="subscription.subscribe.buttontext"/>
</a>
<a href="javascript:document.getElementById('action').value='terminate';document.forms['<portlet:namespace />form_subscribe'].submit()"
   class="button">
   <fmt:message key="subscription.subscribe.operation.terminateall"/>
</a>
<a href="javascript:document.getElementById('action').value='pause';document.forms['<portlet:namespace />form_subscribe'].submit()"
   class="button">
   <fmt:message key="subscription.subscribe.operation.pauseall"/>
</a>
<a href="javascript:document.getElementById('action').value='resume';document.forms['<portlet:namespace />form_subscribe'].submit()"
   class="button">
   <fmt:message key="subscription.subscribe.operation.resumeall"/>
</a>
<br>
</c:when>
<c:otherwise>
   <fmt:message key="subscription.nonewsletter"/>
</c:otherwise>
</c:choose>
</div>
</form>