<%@include file="globals.jsp"
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<cmscedit:head title="reactions.title">
</cmscedit:head>
<body>
<edit:ui-tabs>
   <edit:ui-tab title="${requestScope.newsletterTitle}" active="true">
      #
   </edit:ui-tab>
</edit:ui-tabs>
<div class="editor">
   <div class="body">
      <form method="POST" name="form" action="SubscriptionManagement.do">
         <input type="hidden" name="action" value="listSubscription"/>
         <input type="hidden" name="newsletterId" value="${param.newsletterId}"/>
         <table>
            <tr>
               <td style="width: 110px"><fmt:message key="subscriptiondetail.fullname"/></td>
               <td><input type="text" name="name" value="${param.name}" style="width: 200px"/></td>
            </tr>
            <tr>
               <td><fmt:message key="subscriptiondetail.emailaddress"/></td>
               <td><input type="text" name="email" value="${param.email}" style="width: 200px"/></td>
            </tr>
            <tr>
               <td></td>
               <td><input type="submit" name="submitButton" onclick="document.forms['form'].submit()" value="<fmt:message key='newspubform.submit'/>"/></td>
            </tr>
         </table>
      </form>
   </div>
</div>
<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
      <div class="body">
         <form method="POST" name="operationform" action="SubscriptionImportExportAction.do">
            <input type="hidden" name="action" value="export"/>
            <input type="hidden" name="type" id="action" value="person"/>
            <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/SubscriptionManagement.do">
               <edit:ui-tcolumn title="" width="5%">
                  <input type="checkbox" name="ids" value="${result.id}"/>
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="subscriptionoverview.username" sort="username" width="15%">
                  ${result.username}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="subscriptiondetail.fullname" sort="fullname" width="15%">
                  ${result.fullname}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="subscriptiondetail.emailaddress" sort="email" width="65%">
                  ${result.email}
               </edit:ui-tcolumn>
            </edit:ui-table>
            <c:if test="${resultCount gt 0}">
            <input type="button" name="submitButton" class="submit" onclick="exportsubscription()"
                   value="<fmt:message key="subscriptiondetail.link.exportselect"/>"/>
            <input type="button" name="submitButton" class="submit" onclick="showImportPage()"
                   value="<fmt:message key="subscriptiondetail.link.importcsv"/>"/>
            </c:if>
         </form>
      </div>
   </div>
</div>
</body>
</html>
</mm:content>
<script>
   function showImportPage(){
      document.operationform.action ="SubscriptionManagement.do?action=showImportPage&importType=importCSV&newsletterId=${param.newsletterId}";
      document.operationform.submit();
   }

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
         alert("<fmt:message key='confirm_noselect'/>");
      }

      return false;
   }
</script>
