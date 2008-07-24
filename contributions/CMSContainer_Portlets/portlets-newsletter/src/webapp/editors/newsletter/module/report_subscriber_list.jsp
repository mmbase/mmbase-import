<%@include file="globals.jsp" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<cmscedit:head title="reactions.title">
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="newsletterdetail.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <c:if test="${fn:length(results) gt pagesize || not empty param.name || not empty param.email }">
   <div class="body">
      <form method="POST" name="form" action="SubscriptionManagement.do">
         <input type="hidden" name="action" value="listSubscribers"/>
         <table>
            <tr>
               <td style="width: 150px"><fmt:message key="subscriptiondetail.fullname"/></td>
               <td>
                  <input type="text" name="name" value="${param.name}" style="width: 250px"/>
               </td>
            </tr>
            <tr>
               <td style="width: 150px"><fmt:message key="subscriptiondetail.emailaddress"/></td>
               <td>
                  <input type="text" name="email" value="${param.email}" style="width: 250px"/>
               </td>
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
         <form method="POST" name="operationform" action="SubscriptionImportExportAction.do">
            <input type="hidden" name="action" value="export"/>
            <input type="hidden" name="type" value="person"/>
            <pg:pager maxPageItems="${pagesize}" url="SubscriptionManagement.do">
               <pg:param name="action" value="listSubscribers"/>
               <pg:param name="name" value="${param.name}"/>
               <pg:param name="email" value="${param.email}"/>
               <table>
                  <thead>
                     <th></th>
                     <th><fmt:message key="subscriptionoverview.username"/></th>
                     <th><fmt:message key="subscriptiondetail.fullname"/></th>
                     <th><fmt:message key="subscriptiondetail.emailaddress"/></th>
                  </thead>
                  <tbody>
                        <%--@elvariable id="results" type="java.util.List"--%>
                     <c:forEach items="${results}" var="result">
                        <pg:item>
                           <tr>
                              <td><input type="checkbox" name="ids" value="${result.id}"/></td>
                              <td>
                                 <a href="SubscriptionManagement.do?action=listSubscriptionByPerson&subsriberId=${result.id}">
                                       ${result.email}
                                 </a>
                              </td>
                              <td>${result.fullname}</td>
                              <td>${result.email}</td>
                           </tr>                                                                                              
                        </pg:item>
                     </c:forEach>
                  </tbody>
               </table>
               <%@ include file="pager_index.jsp" %>
            </pg:pager>
            <br/>
            <input type="button" name="submitButton" class="submit"
                   onclick="exportsubscription()"
                   value="<fmt:message key="subscriptiondetail.link.exportselect"/>"/>
         </form>
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
         document.forms['operationform'].submit();
      }
      else {
         alert("You have to select at least one item");
      }

      return false;
   }
</script>


