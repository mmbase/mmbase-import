<%@include file="globals.jsp" 
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<cmscedit:head title="ewsletter.subscription.manage.newsletteroverview">
</cmscedit:head>
<edit:ui-tabs>
   <edit:ui-tab key="newsletteroverview.title" active="true">
      #
   </edit:ui-tab>
</edit:ui-tabs>
<div class="editor">
   <div class="body">
      <form method="POST" name="form" action="SubscriptionManagement.do">
         <input type="hidden" name="action" value="newsletterOverview"/>
         <table border="0">
            <tr>
               <td style="width:110px;"><fmt:message key="newspubform.title"/></td>
               <td><input type="text" name="title" value="${param.title}" style="width: 150px"/></td>
            </tr>
            <tr>
               <td></td>
               <td><input type="submit" name="submitButton" value="<fmt:message key='newspubform.submit'/>"/></td>
            </tr>
         </table>
      </form>
   </div>
</div>
<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletteroverview.title"/>&nbsp;</div></div>
      <div class="body">
      <form method="POST" name="operationform" action="SubscriptionManagement.do">
         <input type="hidden" name="action" />
          <input type="hidden" name="type" value="newsletter"/>
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/SubscriptionManagement.do">
            <edit:ui-tcolumn title="" width="5%">
               <input type="checkbox" name="ids" value="${result.id}"/>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletteroverview.newsletter" sort="title" width="15%" >
               <a href="SubscriptionManagement.do?action=listSubscription&newsletterId=${result.id}">${result.title}</a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="globalstats.total.publications" width="15%"  sort="countpublications" >
               ${result.countpublications}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="globalstats.total.sentsubscriptions" width="15%"  sort="countSentPublicatons" >
               ${result.countSentPublicatons}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="globalstats.total.subscriptions" width="15%" sort="countSubscriptions" >
               ${result.countSubscriptions}
            </edit:ui-tcolumn>
         </edit:ui-table>
         <c:if test="${resultCount gt 0}">
            <input type="button" name="submitButton" class="submit" onclick="exportsubscription()" value="<fmt:message key="subscriptiondetail.link.exportselect"/>"/>
            <input type="button" name="submitButton" onclick="unsubscribeAll()" value="<fmt:message key="globalstats.total.unsubscribeselect"/>"/>
         </c:if>
         </form>
      </div>
      
   </div>
</div>
</html>
</mm:content>
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
         document.forms['operationform'].attributes['action'].value = 'SubscriptionImportExportAction.do';
         document.forms['operationform'].elements('action').value = 'export';
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


