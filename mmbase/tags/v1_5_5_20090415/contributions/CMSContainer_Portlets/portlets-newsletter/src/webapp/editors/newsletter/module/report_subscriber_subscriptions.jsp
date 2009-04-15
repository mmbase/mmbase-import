<%@include file="globals.jsp"
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
   <cmscedit:head title="ewsletter.subscription.manage.newsletteroverview">
   </cmscedit:head>
   <body>
      <edit:ui-tabs>
         <edit:ui-tab key="newsletteroverview.title" active="true">
            #
         </edit:ui-tab>
      </edit:ui-tabs>

      <div class="editor">
         <div class="body">
            <form method="post" name="form" action="SubscriptionManagement.do">
               <input type="hidden" name="action" value="listSubscriptionByPerson"/>
               <input type="hidden" name="subsriberId" value="${requestScope.subsriberId}"/>
               <table border="0">
                  <tr>
                     <td style="width: 110px"><fmt:message key="newspubform.title"/></td>
                     <td><input type="text" name="title" value="${param.title}" style="width: 150px"/></td>
                  </tr>
                  <tr>
                     <td></td>
                     <td><input type="submit" name="submitButton" onclick="document.forms['form'].submit()" value="<fmt:message key='newsletter.subscriber.search'/>"/></td>
                  </tr>
               </table>
            </form>
         </div>
         <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
         <div class="body">
            <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/SubscriptionManagement.do">
               <edit:ui-tcolumn titlekey="newsletteroverview.newsletter" sort="title" width="15%">
                  ${result.title}
               </edit:ui-tcolumn>
               <edit:ui-tcolumn titlekey="subscriptionoverview.status" width="85%">
                  ${result.status}
               </edit:ui-tcolumn>
            </edit:ui-table>
         </div>
      </div>
   </body>
</html>
</mm:content>