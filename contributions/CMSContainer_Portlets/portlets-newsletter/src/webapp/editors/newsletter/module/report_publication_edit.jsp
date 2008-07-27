<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
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
         <a href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=false&forward=newslettermanage" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" title="<fmt:message key='site.newsletterpublication.new.blank'/>">
            <fmt:message key="site.newsletterpublication.new.blank"/>
         </a>
      </p>
      <p>
         <a href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=true&forward=newslettermanage" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" title="<fmt:message key='site.newsletterpublication.new.withcontent'/>">
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
         <mm:import externid="results" jspvar="nodeList" vartype="List" />
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer"/>
         <mm:import externid="offset" jspvar="offset" vartype="Integer"/>
         <c:if test="${resultCount > 0}">
            <%@include file="../../repository/searchpages.jsp" %>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
               <thead>
                  <th width="15%">&nbsp;</th>
                  <th width="20%">
                     <a href="javascript:orderBy('title');">
                        <fmt:message key="newsletter.publication.result.title"/>
                     </a>
                  </th>
                  <th width="20%">
                     <a href="javascript:orderBy('subject');">
                        <fmt:message key="newsletter.publication.result.subject"/>
                     </a>
                  </th>
                  <th width="15%">
                     <a href="javascript:orderBy('lastmodifier');">
                        <fmt:message key="newsletter.publication.result.lastmodifier"/>
                     </a>
                  </th>
                  <th width="30%">
                     <a href="javascript:orderBy('lastmodifieddate');">
                        <fmt:message key="newsletter.publication.result.modificationdate"/>
                     </a>
                  </th>
               </thead>
               <c:forEach items="${results}" var="result">
                  <tr>
                     <td width="15%">
                        <a href="../site/NavigatorPanel.do?nodeId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.gif'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.editproperty'/>"></a>
                        <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16" title="<fmt:message key='site.newsletterpublication.edit'/>"></a>
                        <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=publicationedit"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='site.newsletterpublication.remove'/>"></a>
                        <a href="../newsletter/NewsletterPublicationTest.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_go.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendemail'/>"></a>
                        <a href="../newsletter/NewsletterPublicationPublish.do?number=${result.id}&forward=publicationedit&newsletterId=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_error.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.sendpublication'/>"></a>
                        <a href="../usermanagement/pagerights.jsp?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/rights.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.userright'/>"></a>
                     </td>
                     <td width="20%" >${result.title}</td>
                     <td width="20%" >${result.subject}</td>
                     <td width="15%" >${result.lastmodifier}</td>
                     <td width="30%" >${result.lastmodifieddate}</td>
                  </tr>
               </c:forEach>
            </table>
         </c:if>

         <c:if test="${resultCount == 0}">
            <fmt:message key="newsletter.publication.noresult"/>
         </c:if>
         <c:if test="${resultCount > 0}">
            <%@include file="../../repository/searchpages.jsp" %>
         </c:if>
      </div>
   </div>
   </body>
</html:html>
</mm:content>
