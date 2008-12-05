<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
      <link href="<c:url value="/editors/css/newsletter.css"/>" rel="stylesheet" type="text/css">
   </cmscedit:head>
   <body>
   <edit:ui-tabs>
      <edit:ui-tab key="newsletter.publication.tabs.edit" >
         NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.statistics">
         NewsletterPublicationStatisticSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.subscribers" active="true">
         #
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.terms">
         NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
   </edit:ui-tabs>

   <div class="editor">
      <div class="body">
         <ul class="shortcuts">
             <li class="new" style="text-decoration: none;">
            <c:url var="addSuscriberUrl" value="/editors/community/SearchConditionalUser.do">
               <c:param name="method" value="searchCandidateSubscriber"/>
               <c:param name="newsletterId" value="${requestScope.newsletterId}"/>
               <c:param name="path" value="${forwardPath}"/>
            </c:url>
            <c:url var="addUserUrl" value="/editors/community/userAddInitAction.do">
               <c:param name="forward" value="newslettersubscribers"/>
               <c:param name="newsletterId" value="${requestScope.newsletterId}"/>
               <c:param name="path" value="${forwardPath}"/>
            </c:url>

               <a  href="${addSuscriberUrl}" title="<fmt:message key='newsletter.publication.link.newsubscriber'/>">
                  <fmt:message key="newsletter.publication.link.newsubscriber"/>
               </a>
          </li>
          <li class="new" style="text-decoration: none;">
               <a  href="${addUserUrl}" title="<fmt:message key='newsletter.publication.link.newuser'/>">
                  <fmt:message key="newsletter.publication.link.newuser"/>
               </a>
          </li>
      </ul>

         <html:form action="editors/newsletter/NewsletterPublicationSubscriberSearch.do">
            <input type="hidden" name="method" value="subScriberSearch"/>
            <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
            <table border="0" cellspacing="0" cellpadding="0">
               <tr>
                  <td width="110px"><fmt:message key="newsletter.publication.result.fullname"/></td>
                  <td>
                     <html:text property="fullname" size="30"/>
                  </td>
               </tr>
               <tr>
                     <td><fmt:message key="newsletter.publication.result.username"/></td>
                     <td><html:text property="username" size="30"/></td>
               </tr>
               <tr>
                     <td><fmt:message key="newsletter.publication.result.email"/></td>
                     <td><html:text property="email" size="30"/></td>
               </tr>
               <tr>
                     <td><fmt:message key="newsletter.publication.result.terms"/></td>
                     <td><html:text property="term" size="30"/></td>
               </tr>
               <tr>
                     <td>&nbsp;</td>
                     <td><html:submit><fmt:message key="newsletter.publication.search"/></html:submit></td>
               </tr>
            </table>
         </html:form>
      </div>

      <div class="editor">
         <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
         <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterPublicationSubscriberSearch.do">
            <edit:ui-tcolumn title="" width="5%">
               <a href="NewsletterSubscriberDelete.do?newsletterId=${requestScope.newsletterId}&authid=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.user.unsubscribe'/>"/></a>
               <a href="../community/userAddInitAction.do?authid=${result.id}&newsletterId=${requestScope.newsletterId}&forward=newslettersubscribers"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16"  title="<fmt:message key='newsletter.icons.title.edituser'/>"/></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.fullname" sort="fullname" width="20%">
               ${result.fullname}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.username" sort="username" width="20%">
               ${result.username}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.email" sort="email" width="20%">
               ${result.email}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.newsletter" width="20%">
               ${result.newsletters}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.terms" width="20%">
               ${result.terms}
            </edit:ui-tcolumn>
         </edit:ui-table>
         </div>
      </div>
   </div>
   </body>
</html>
</mm:content>
