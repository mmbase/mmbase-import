<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="globalstats.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <div class="body">
      <table width="50%">
         <tr>
            <td><fmt:message key="globalstats.total.newsletters"/></td>
            <td>${requestScope.newslettercount}</td>
         </tr>
         <tr>
            <td><fmt:message key="globalstats.total.terms"/></td>
            <td>${requestScope.termcount}</td>
         </tr>
         <tr>
            <td><fmt:message key="globalstats.total.publications"/></td>
            <td>${requestScope.publicationcount}</td>
         </tr>
         <tr>
            <td><fmt:message key="globalstats.total.subscriptions"/></td>
            <td>${requestScope.subscriptioncount}</td>
         </tr>
      </table>
      <div>
         <p>
            <a href="SubscriptionManagement.do?action=newsletterOverview">
               <fmt:message key="index.link.newsletteroverview"/>
            </a>
         </p>

         <p>
            <a href="SubscriptionManagement.do?action=listSubscribers">
               <fmt:message key="index.link.subscriptionoverview"/>
            </a>
         </p>

         <p>
            <a href="SubscriptionImportExportAction.do?action=export">
               <fmt:message key="index.link.exportall"/>
            </a>
         </p>

         <p>
            <a href="SubscriptionManagement.do?action=showImportPage">
               <fmt:message key="index.link.import"/>
            </a>
         </p>
         <p>
            <a href="ShowNewsletters.do?action=show">
               <fmt:message key="index.link.newsletterstatistic"/>
            </a>
         </p>
      </div>
   </div>
</div>
