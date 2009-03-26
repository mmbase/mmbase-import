<%@ page import="com.finalist.newsletter.domain.Subscription" 
%><%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<fmt:setBundle basename="portlets-newslettersubscription" scope="request"/>
<c:set var="contextPath">
   <%=request.getContextPath()%>/editors/newsletter/Subscribe.do
</c:set>
<script language="javascript">
   function modifyStatus(newsletterId, box) {
      new Ajax.Request("${contextPath}",
      {
         method: 'get',
         parameters: {newsletterId: newsletterId,select: box.checked ,action: 'modifyStatus'}
      }
      );
   }
</script>

<div class="content">

<form method="POST" name="<portlet:namespace />form_subscribe" action="<cmsc:actionURL/>">

<h3><fmt:message key="subscription.subscribe.title"/></h3>

<c:choose>
<c:when test="${fn:length(subscriptionList) > 0}">

   <table>
      <tr>
         <td><fmt:message key="subscription.view.list.activated"/></td>
         <td><fmt:message key="subscription.view.list.title"/></td>
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
                  ${status ne 'INACTIVE' ? 'checked="checked"' : ''}
                     />
            </td>
            <td>
                ${subscription.newsletter.title}
            </td>
         </tr>
      </c:forEach>
   </table>
   
   <p>
      <input type="hidden" name="action" id="action"/>
      <a href="javascript:document.forms['<portlet:namespace />form_subscribe'].submit()" class="button">
         <fmt:message key="subscription.subscribe.save"/>
      </a>
   </p>

</c:when>
<c:otherwise>
   <p><fmt:message key="subscription.nonewsletter"/></p>
</c:otherwise>
</c:choose>
</form>
</div>