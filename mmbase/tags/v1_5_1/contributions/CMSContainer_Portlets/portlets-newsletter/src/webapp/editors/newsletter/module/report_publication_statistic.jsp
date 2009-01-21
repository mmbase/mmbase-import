<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
   </cmscedit:head>
   <body>
   <edit:ui-tabs>
      <edit:ui-tab key="newsletter.publication.tabs.edit" >
         NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
       <edit:ui-tab key="newsletter.publication.tabs.statistics" active="true">
         #
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.subscribers">
         NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.terms">
         NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
   </edit:ui-tabs>

   <div class="editor">
      <div  class="body">
         <html:form action="editors/newsletter/NewsletterPublicationStatisticSearch.do">
            <input type="hidden" name="method" value="searchPublicationStatistic"/>
            <%@include file="report_publication_search.jsp"%>
         </html:form>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
      <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterPublicationStatisticSearch.do">
            <edit:ui-tcolumn title="" width="5%">
               <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=newsletterstatistics"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.remove'/>"/></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.title" sort="title" width="15%">
               <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=newsletterstatistics" title="<fmt:message key='site.newsletteredition.edit'/>">
                  ${result.title}
               </a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.subject" sort="subject" width="15%">
               ${result.subject}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.sentat" sort="publishdate" width="15%">
               ${result.sendtime}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.subscriptions" sort="subscriptions" width="15%">
               ${result.subscriptions}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.sentsuccess" width="15%">
               ${result.sendsuccessful}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.bounced" sort="bounced" width="20%">
               ${result.bounced}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </div>
   </div>
   </body>
</html:html>
</mm:content>
