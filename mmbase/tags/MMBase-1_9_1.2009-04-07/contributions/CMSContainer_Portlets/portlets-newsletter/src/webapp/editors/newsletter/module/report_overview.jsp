<%@include file="globals.jsp" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="index.title"/>
<body>
   <edit:ui-tabs>
      <edit:ui-tab key="index.title" active="true">
         #
      </edit:ui-tab>
   </edit:ui-tabs>
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
               <fmt:message key="index.link.allsubscriber"/>
            </h1>
         </td>
         <td width="33%">
            <h1 style="font-size:14px; font-weight:normal; text-transform:uppercase; letter-spacing:2px; color:#418001;">
               <fmt:message key="index.link.title.editionstats"/>
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
            <p><a href="NewsletterPublicationAction.do"><fmt:message key="index.link.alleditions"/></a></p>
            <p><a href="module/NewsletterBounceAction.do?method=list"><fmt:message key="index.link.bouncedemail"/></a></p>
         </td>
      </tr>
</table>

</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="index.title.task"/>&nbsp;</div></div>
   <div class="body"> 
      <div style="padding-left:10px;">
      <p><a href="module/NewsletterTermAction.do?method=list&init=true"><fmt:message key="index.link.manageterm"/></a></p>
      <p><a href="SubscriptionImportExportAction.do?action=export"><fmt:message key="index.link.exportall"/></a></p>
      <p><a href="SubscriptionManagement.do?action=showImportPage"><fmt:message key="index.link.import"/></a></p>
      <p><a href="ShowNewsletters.do?action=show"><fmt:message key="index.link.newsletterstatistic"/></a></p>
      </div>
      <br/>
   </div>
</div>
</body>
</html:html>
</mm:content>