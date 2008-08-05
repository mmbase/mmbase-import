<%@include file="globals.jsp" 
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" %>
<form method="post" name="operationform" action="SubscriptionManagement.do">
   <input type="hidden" name="action" id="action"/>
   <input type="hidden" name="type" id="action" value="newsletter"/>
      <table>
         <thead>
            <th width="3%"></th>
            <th width="5%"></th>
            <th width="15%"><fmt:message key="newsletteroverview.newsletter"/></th>
            <th width="15%"><fmt:message key="globalstats.total.publications"/></th>
            <th width="15%"><fmt:message key="globalstats.total.sentsubscriptions"/></th>
            <th width="47%"><fmt:message key="globalstats.total.subscriptions"/></th>
         </thead>
         <tbody>
            <c:forEach items="${results}" var="result">
                  <tr <mm:even inverse="true">class="swap"</mm:even>>
                     <td>
                        <input type="checkbox" name="ids" value="${result.id}"/>
                     </td>
                     <td>
                        <a href="NewsletterDelete.do?number=${result.id}&amp;remove='true'&amp;forward='manage'"><img src="../gfx/icons/delete.png" align="top" title="<fmt:message key='site.newsletter.remove'/>"/></a>
                        <a href="NewsletterEdit.do?number=${result.id}&amp;forward=manage"><img src="../gfx/icons/edit_defaults.png" align="top" title="<fmt:message key='site.newsletter.edit'/>"/></a>
                     </td>
                     <td>
                        <a href="NewsletterPublicationManagement.do?newsletterId=${result.id}">
                           ${result.title}
                        </a>
                     </td>
                     <td>${result.countpublications}</td>
                     <td>${result.countSentPublicatons}</td>
                     <td>${result.countSubscriptions}</td>
                  </tr>
            </c:forEach>
         </tbody>
      </table>
      <br/>
   <input type="button" name="submitButton" class="submit"
          onclick="exportsubscription()"
          value="<fmt:message key="subscriptiondetail.link.exportselect"/>"/>
</form>

<script type="text/javascript">
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
</script>