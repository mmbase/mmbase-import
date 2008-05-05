<%@include file="globals.jsp" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="newsletter.subscription.manage.newsletteroverview"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <c:if test="${fn:length(results) gt pagesize || not empty param.query_parameter_title }">
   <div class="body">
      <form method="POST" name="form" action="SubscriptionManagement.do">
         <input type="hidden" name="action" value="newsletterOverview"/>
         <table border="0">
            <tr>
               <td style="width: 150px">Title</td>
               <td><input type="text" name="query_parameter_title" value="${param.query_parameter_title}"
                          style="width: 250px"/></td>
            </tr>
            <tr>
               <td colspan="2">
                  <input type="submit" name="submitButton"
                         onclick="document.forms['form'].submit()" value="Search"/>
               </td>
            </tr>
         </table>
      </form>

      <div class="ruler_green">
         <div>Reactions found</div>
      </div>
      </c:if>
      <div class="body">
         <form method="POST" name="operationform" action="SubscriptionManagement.do">
            <input type="hidden" name="action" id="action"/>
            <input type="hidden" name="type" id="type" value="newsletter"/>
            <pg:pager maxPageItems="${pagesize}" url="SubscriptionManagement.do">
               <pg:param name="action" value="newsletterOverview"/>
               <pg:param name="query_parameter_title" value="${param.query_parameter_title}"/>
               <table>
                  <thead>
                     <th></th>
                     <th>title</th>
                     <th>number of publications</th>
                     <th>sent publications</th>
                     <th>number of subscriptions</th>
                  </thead>
                  <tbody>
                        <%--@elvariable id="results" type="java.util.List"--%>
                     <c:forEach items="${results}" var="result">
                        <pg:item>
                           <tr>
                              <td><input type="checkbox" name="newsletterIds" value="${result.id}"/></td>
                              <td><a href="SubscriptionManagement.do?action=newsletterDetail&nesletterId=${result.id}">${result.title}</a></td>
                              <td>${result.countpublications}</td>
                              <td>${result.countSentPublicatons}</td>
                              <td>${result.countSubscriptions}</td>
                           </tr>
                        </pg:item>
                     </c:forEach>
                  </tbody>
               </table>
               <%@ include file="pager_index.jsp" %>
            </pg:pager>

            <ul>
               <li>
                  <input type="submit" name="submitButton"
                         onclick="document.getElementById('action').value='exportSusbscriptions';document.forms['operationform'].submit()"
                         value="Export subscriptions"/>
               </li>
               <li>
                  <a href="#">Unsubscribe selected subscriptions</a>
               </li>
            </ul>
         </form>
      </div>

   </div>
</div>


