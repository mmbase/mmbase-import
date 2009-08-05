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
      <edit:ui-tab key="newsletterdetail.title" active="true">
         #
      </edit:ui-tab>
   </edit:ui-tabs>
   <div class="editor">
      <div class="body">
         <form method="POST" name="form" action="SubscriptionManagement.do">
            <input type="hidden" name="action" value="listSubscribers"/>
            <table>
               <tr>
                  <td style="width:110px;"><fmt:message key="subscriptiondetail.fullname"/></td>
                  <td><input type="text" name="name" value="${param.name}" style="width: 150px"/></td>
               </tr>
               <tr>
                  <td><fmt:message key="subscriptiondetail.emailaddress"/></td>
                  <td><input type="text" name="email" value="${param.email}" style="width: 150px"/></td>
               </tr>
               <tr>
                  <td></td>
                  <td><input type="submit" name="submitButton" onclick="document.forms['form'].submit()" value="<fmt:message key='newsletter.subscriber.search'/>"/></td>
               </tr>
            </table>
         </form>
      </div>
   </div>
   <div class="editor">
      <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
      <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/SubscriptionManagement.do">
            <edit:ui-tcolumn title="" width="5%">
               <input type="checkbox" name="ids" value="${result.id}"/>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="subscriptionoverview.username" sort="username" width="15%">
               <a href="SubscriptionManagement.do?action=listSubscriptionByPerson&subsriberId=${result.id}">${result.username}</a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="subscriptiondetail.fullname" sort="fullname" width="15%">
               ${result.fullname}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="subscriptiondetail.emailaddress" sort="email" width="65%">
               ${result.email}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </div>
   </div>
</body>
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
         document.forms['operationform'].submit();
      }
      else {
         alert("You have to select at least one item");
      }

      return false;
   }
</script>


