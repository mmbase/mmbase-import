<%@ page import="com.finalist.newsletter.domain.Subscription" 
%><%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="newsletter_subscription">
<form method="post" name="<portlet:namespace />form_subscribe" action="<cmsc:actionURL/>">
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
                            value="${newsletterId}"
                            name="subscriptions"
                            id="subscription-${subscription.id}"                      
                        ${status ne 'INACTIVE' ? 'checked="checked"' : ''}
                           />
                  </td>
                  <td>
                      ${subscription.newsletter.title}
                  </td>
               </tr>
            </c:forEach>
         </table>
         
         <input type="submit" value="<fmt:message key="subscription.subscribe.save"/>">
      
      </c:when>
      <c:otherwise>
         <p><fmt:message key="subscription.nonewsletter"/></p>
      </c:otherwise>
   </c:choose>
   </form>
</div>