<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
      <link href="<c:url value="/editors/css/newsletter.css"/>" rel="stylesheet" type="text/css">
   </cmscedit:head>

   <body>
   <edit:ui-tabs>
      <edit:ui-tab key="newsletter.publication.tabs.edit" active="true">
         #
      </edit:ui-tab>
       <edit:ui-tab key="newsletter.publication.tabs.statistics">
         NewsletterPublicationStatisticSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
      <edit:ui-tab key="newsletter.publication.tabs.subscribers">
         NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
       <edit:ui-tab key="newsletter.publication.tabs.terms">
         NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}
      </edit:ui-tab>
   </edit:ui-tabs>

   <div class="editor">
      <div style="padding-left:10px;">
      <p>
         <a class="addemail" href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=false&forward=newslettermanage" title="<fmt:message key='site.newsletterpublication.new.blank'/>">
            <fmt:message key="site.newsletterpublication.new.blank"/>
         </a>
      </p>
      <p>
         <a class="addemail" href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=true&forward=newslettermanage" title="<fmt:message key='site.newsletterpublication.new.withcontent'/>">
            <fmt:message key="site.newsletterpublication.new.withcontent"/>
         </a>
      </p>
      </div>
      <div style="padding-left:10px;">
         <html:form action="editors/newsletter/NewsletterPublicationManagement.do">
            <input type="hidden" name="method" value="searchPublication"/>
            <%@include file="report_publication_search.jsp"%>
         </html:form>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
      <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterPublicationManagement.do">
            <edit:ui-tcolumn title="" width="5%">
               <a href="../site/NavigatorPanel.do?nodeId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.gif'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.editproperty'/>"></a>
               <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='site.newsletterpublication.remove'/>"></a>
               <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16" title="<fmt:message key='site.newsletterpublication.edit'/>"></a>
               <a href="../newsletter/NewsletterPublicationTest.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_go.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendemail'/>"></a>
               <a href="../newsletter/NewsletterPublicationPublish.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_error.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendpublication'/>"></a>
               <a href="../usermanagement/pagerights.jsp?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/rights.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.userright'/>"></a>
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.title" sort="title" width="25%">
               ${result.title}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.subject" sort="subject" width="20%">
               ${result.subject}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.lastmodifier" sort="lastmodifier" width="20%">
               ${result.lastmodifier}
            </edit:ui-tcolumn>
            <edit:ui-tcolumn titlekey="newsletter.publication.result.modificationdate" sort="lastmodifieddate" width="30%">
               ${result.lastmodifieddate}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </div>
   </div>
   </body>
</html:html>
</mm:content>
