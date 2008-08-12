<%@include file="globals.jsp"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc"
%><%@ taglib uri="http://jsptags.com/tags/navigation/pager" prefix="pg"
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
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
            <form method="POST" name="form" action="SubscriptionManagement.do">
               <input type="hidden" name="action" value="listSubscriptionByPerson"/>
               <input type="hidden" name="subsriberId" value="${requestScope.subsriberId}"/>
               <table border="0">
                  <tr>
                     <td style="width: 100px"><fmt:message key="newspubform.title"/></td>
                     <td><input type="text" name="title" value="${param.title}" style="width: 200px"/></td>
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
</mm:content>


