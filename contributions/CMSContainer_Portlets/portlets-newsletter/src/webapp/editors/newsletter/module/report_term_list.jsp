<%@include file="globals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<cmscedit:head title="ewsletter.subscription.manage.newsletteroverview">
</cmscedit:head>
<div class="editor">
      <div class="body">
         <c:choose>
            <c:when test="${fn:length(results) gt 0}">
               <form method="POST" name="operationform" action="SubscriptionManagement.do">
                  <input type="hidden" name="action" id="action"/>
                  <input type="hidden" name="type" id="action" value="newsletter"/>
                  <pg:pager maxPageItems="${pagesize}" url="SubscriptionManagement.do">
                     <pg:param name="action" value="newsletterOverview"/>
                     <pg:param name="query_parameter_title" value="${param.query_parameter_title}"/>
                     <table>
                        <thead>
                           <th></th>
                           <th><fmt:message key="newsletteroverview.newsletter"/></th>
                           <th><fmt:message key="globalstats.total.publications"/></th>
                           <th><fmt:message key="globalstats.total.sentsubscriptions"/></th>
                           <th><fmt:message key="globalstats.total.subscriptions"/></th>
                        </thead>
                        <tbody>
                              <%--@elvariable id="results" type="java.util.List"--%>
                           <c:forEach items="${results}" var="result">
                              <pg:item>
                                 <tr>
                                    <td><input type="checkbox" name="ids" value="${result.id}"/></td>
                                    <td>
                                       <a href="SubscriptionManagement.do?action=listSubscription&newsletterId=${result.id}">
                                             ${result.title}
                                       </a>
                                    </td>
                                    <td>${result.countpublications}</td>
                                    <td>${result.countSentPublicatons}</td>
                                    <td>${result.countSubscriptions}</td>
                                 </tr>
                              </pg:item>
                           </c:forEach>
                        </tbody>
                     </table>
                     <%@ include file="pager_index.jsp" %>
                     <br>
                  </pg:pager>
                  <input type="button" name="submitButton" class="submit"
                         onclick="exportsubscription()"
                         value="<fmt:message key="subscriptiondetail.link.exportselect"/>"/>
                  <input type="button" name="submitButton"
                         onclick="unsubscribeAll()"
                         value="<fmt:message key="globalstats.total.unsubscribeselect"/>"/>
               </form>
            </c:when>
            <c:otherwise>
               <fmt:message key="error.no_items"/>
            </c:otherwise>
         </c:choose>
      </div>
   </div>
