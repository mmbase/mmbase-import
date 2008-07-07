<%@include file="globals.jsp" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>

<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="index.title"/></a>
         </div>
      </div>
   </div>
</div>
<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletteroverview.newsletter"/>&nbsp;</div></div>
   <div class="body">
      <c:choose>
         <c:when test="${fn:length(results) gt 0}">
            <%@ include file="report_newsletter_overview_list.jsp" %>
         </c:when>
         <c:otherwise>
            <fmt:message key="error.no_items"/>
         </c:otherwise>
      </c:choose>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="index.title.overview"/>&nbsp;</div></div>
</div>
<div class="editor dashboard">
   <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding-left:10px; font-size:12px;">
      <tr>
         <td width="33%">
         <h1 style="font-size:14px; font-weight:normal; text-transform:uppercase; letter-spacing:2px; color:#418001;">
            <fmt:message key="index.link.title.subscriptionstats"/>
         </h1>
         </td>
         <td width="33%">
            <h1 style="font-size:14px; font-weight:normal; text-transform:uppercase; letter-spacing:2px; color:#418001;">
               <fmt:message key="index.link.title.publicationstats"/>
            </h1>
         </td>
         <td width="33%">
            <h1 style="font-size:14px; font-weight:normal; text-transform:uppercase; letter-spacing:2px; color:#418001;">
               <fmt:message key="index.link.title.publicationstats"/>
            </h1>
         </td>
   </tr>
   <tr>
         <td>
            <p>
               <a href="SubscriptionManagement.do?action=newsletterOverview">
                  <fmt:message key="index.link.newsletteroverview"/>
               </a>
            </p>
            <p>&nbsp;</p>
         </td>
         <td>
            <p>
            <a href="SubscriptionManagement.do?action=listSubscribers">
               <fmt:message key="index.link.allsubscriber"/>
            </a>
            </p>
            <p>&nbsp;</p>
         </td>
         <td>
            <p><a href="NewletterPublicationInitAction.do"><fmt:message key="index.link.allpublication"/></a></p>
            <p><a href="#"><fmt:message key="index.link.bouncedemail"/></a></p>
         </td>
      </tr>
</table>

</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="index.title.task"/>&nbsp;</div></div>
</div>
<div class="editor">
   <div class="body"> 
      <table>
      <tr><td><fmt:message key="index.link.manageterm"/></td></tr>
      <tr><td><a href="SubscriptionImportExportAction.do?action=export"><fmt:message key="index.link.exportall"/></a></td></tr>
      <tr><td><a href="SubscriptionManagement.do?action=showImportPage"><fmt:message key="index.link.import"/></a></td></tr>
      <tr><td><a href="ShowNewsletters.do?action=show"><fmt:message key="index.link.newsletterstatistic"/></a></td></tr>
   </div>
</div>
