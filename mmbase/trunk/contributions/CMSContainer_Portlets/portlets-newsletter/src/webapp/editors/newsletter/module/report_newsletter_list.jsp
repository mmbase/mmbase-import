<%@include file="globals.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>

<cmscedit:head title="ewsletter.subscription.manage.newsletteroverview">
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="newsletteroverview.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <c:if test="${fn:length(results) gt pagesize || not empty param.title }">
   <div class="body">
      <form method="POST" name="form" action="SubscriptionManagement.do">
         <input type="hidden" name="action" value="newsletterOverview"/>
         <table border="0">
            <tr>
               <td style="width: 150px">Title</td>
               <td><input type="text" name="title" value="${param.title}" style="width: 250px"/></td>
            </tr>
            <tr>
               <td colspan="2">
                  <input type="submit" name="submitButton" onclick="document.forms['form'].submit()" value="Search"/>
               </td>
            </tr>
         </table>
      </form>
   </div>
   </c:if>
</div>
<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletteroverview.title"/>&nbsp;</div></div>
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
                           <c:set var="useSwapStyle">true</c:set>
                           <c:forEach items="${results}" var="result">
                              <pg:item>
                                 <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
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
                                 <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
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
</div>
<script>
   function exportsubscription() {
      var subscriptions = document.getElementsByName('ids');
      var hasSelection = false;
      for (var i = 0; i < subscriptions.length; i ++) {
         if (subscriptions[i].checked) {
            hasSelection = true;
            break;
         }
      }

      if (hasSelection) {
         document.forms['operationform'].action = 'SubscriptionImportExportAction.do';
         document.getElementById('action').value = 'export';
         document.forms['operationform'].submit();
      }
      else {
         alert('<fmt:message key="confirm_noselect"/>');
      }

      return false;
   }

   function unsubscribeAll() {

      var subscriptions = document.getElementsByName('ids');
      var hasSelection = false;
      for (var i = 0; i < subscriptions.length; i ++) {
         if (subscriptions[i].checked) {
            hasSelection = true;
            break;
         }
      }

      if (hasSelection) {
         var confirm = window.confirm('<fmt:message key="confirm.unsubscribe.newsletter"/>')
         if (!confirm) {
            return false;
         }
         document.getElementById('action').value = 'unsubscribe';
         document.forms['operationform'].submit();
      }
      else {
         alert('<fmt:message key="confirm_noselect"/>');
      }

      return false;
   }
</script>


