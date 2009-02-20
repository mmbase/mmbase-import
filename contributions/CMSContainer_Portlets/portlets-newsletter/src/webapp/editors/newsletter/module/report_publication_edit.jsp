<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
      <div class="body">
         <ul class="shortcuts">
             <li class="new" style="text-decoration: none;">
            <a  href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=false&forward=newslettermanage" title="<fmt:message key='site.newsletteredition.new.blank'/>">
               <fmt:message key="site.newsletteredition.new.blank"/>
            </a>
          </li>
          <li class="new" style="text-decoration: none;">
         <a href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=true&forward=newslettermanage" title="<fmt:message key='site.newsletteredition.new.withcontent'/>">
            <fmt:message key="site.newsletteredition.new.withcontent"/>
         </a>
          </li>
         </ul>
      
         <html:form action="editors/newsletter/NewsletterPublicationManagement.do">
            <input type="hidden" name="method" value="searchPublication"/>
            <%@include file="report_publication_search.jsp"%>
         </html:form>
      </div>
   </div>

   <div class="editor">
      <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
      <c:if test="${not empty errors}">
	  <div style="color:red">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key='newsletter.edition.errors'><fmt:param value="${errors}"/> </fmt:message>&nbsp;</div>
	  </c:if>
	  <c:remove var="errors"/>
	  <c:if test="${not empty needajax}">
	  <script language="JavaScript">workfor("<cmsc:staticurl page='/editors/newsletter/NewsletterEditionFreezeAjax.do?'/>","${needajax}");</script>
	  <div id="needajax" style="color:red">&nbsp;&nbsp;&nbsp;&nbsp;<img src="<cmsc:staticurl page='/images/loading.gif'/>" alt=""/>&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key='newsletter.edition.auto'><fmt:param value="${needajax}"/> </fmt:message>&nbsp;</div>
	  <div id="working" style="display:none;color:red">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key='newsletter.edition.fail'><fmt:param value="${needajax}"/> </fmt:message>&nbsp;</div>
	  </c:if>
	  <c:remove var="needajax"/>
      <div class="body">
         <edit:ui-table items="${results}" var="result" size="${resultCount}" requestURI="/editors/newsletter/NewsletterPublicationManagement.do">
            <edit:ui-tcolumn title="" width="5%"><nobr>
             <cmsc:rights nodeNumber="${result.id}" var="rights"/>
               <c:if test="${result.process_status == 'concept edition'}">
               <a href="../site/NavigatorPanel.do?nodeId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.gif'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.editproperty'/>"></a>
               </c:if>
               <c:if test="${result.process_status != 'concept edition'}">
               <img src="<cmsc:staticurl page='/editors/gfx/icons/edit_gray.gif'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.editproperty'/>">
               </c:if>
               <c:if test="${rights == 'chiefeditor' || rights == 'webmaster'}">
               <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.remove'/>"></a>
               </c:if>
               <c:if test="${rights == 'chiefeditor' || rights == 'webmaster' || rights == 'editor' }">
                 <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.edit'/>"></a>
               </c:if>
               <c:if test="${rights == 'chiefeditor' || rights == 'webmaster' || rights == 'editor' || rights == 'writer' }">
               <a href="../newsletter/NewsletterPublicationTest.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_go.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendemail'/>"></a>
               <a href="../newsletter/NewsletterPublicationPublish.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_error.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendedition'/>"></a>
               </c:if>
               <c:if test="${result.process_status == 'concept edition' && (rights == 'chiefeditor' || rights == 'webmaster' || rights == 'editor' || rights == 'writer')}">
                  <a   href="../newsletter/NewsletterEditionFreeze.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/status_finished.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.freeze'/>"></a>
               </c:if>
               <c:if test="${result.process_status == 'Frozen' && (rights == 'chiefeditor' || rights == 'webmaster' || rights == 'editor' || rights == 'writer')}">
                  <a href="../newsletter/NewsletterEditionDefrost.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/status_approved.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.defrost'/>"></a>
                  <a href="../newsletter/NewsletterEditionApprove.do?number=${result.id}&newsletterId=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/status_published.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.approve'/>"></a>
               </c:if>
                <c:if test="${result.process_status=='Approved' && (rights == 'chiefeditor' || rights == 'webmaster' || rights == 'editor' || rights == 'writer')}">
                  <a href="../newsletter/NewsletterEditionRevoke.do?number=${result.id}&newsletterId=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/status_onlive.png'/>" width="16" height="16" title="<fmt:message key='site.newsletteredition.revokeapproval'/>"></a></c:if>
               <a href="../usermanagement/pagerights.jsp?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/rights.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.userright'/>"></a></nobr>
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
            <edit:ui-tcolumn titlekey="newsletter.publication.result.status" sort="process_status" width="30%">
               ${result.process_status}
            </edit:ui-tcolumn>
         </edit:ui-table>
      </div>
   </div>
   </body>
</html:html>
</mm:content>
