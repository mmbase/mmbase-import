<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
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
         <edit:ui-tab key="newsletter.publication.tabs.statistics" >
            NewsletterPublicationStatisticSearch.do?newsletterId=${requestScope.newsletterId}
         </edit:ui-tab>
         <edit:ui-tab key="newsletter.publication.tabs.subscribers">
            NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}
         </edit:ui-tab>
         <edit:ui-tab key="newsletter.publication.tabs.terms" active="true">
            #
         </edit:ui-tab>
      </edit:ui-tabs>

   <div class="editor">
      <div style="padding-left:10px;">
      
      </div>

      <div class="body">
         <div style="padding-left:10px">
            <a class="addemail" href="module/NewsletterTermAction.do?method=list&newsletterId=${requestScope.newsletterId}" title="<fmt:message key='newsletter.publication.search.linkterm'/>">
               <fmt:message key="newsletter.publication.search.linkterm"/>
            </a>
         </div>
         <form method="post" action="NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}">
            <table width="50%" border="0" cellspacing="0" cellpadding="0">
               <tr>
                  <td width="110px"><fmt:message key="newsletter.publication.search.link.name"/></td>
                  <td width="150px"><input type="text" name="name" size="30" width="150px"/></td>
                  <td>
                     <input type="submit" value="Search"/>
                     <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
                  </td>
               </tr>
            </table>
         </form>
         <br/>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green">
         <div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div>
      </div>
      <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterTermSearch.do">
            <edit:ui-tcolumn title="" width="5%">
               <a href="../../editors/newsletter/NewsletterTermDelete.do?newsletterId=${requestScope.newsletterId}&termId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.term.unlink'/>"></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.search.link.name" sort="name" width="95%">
               ${result.name}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </div>
   </div>
   </body>
</html>
</mm:content>
