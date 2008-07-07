<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>
<script src="../repository/search.js" type="text/javascript"></script>
<div class="tabs">
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#"><fmt:message key="newsletter.publication.tabs.edit"/></a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}&searchForwardType=statistics">
               <fmt:message key="newsletter.publication.tabs.statistics"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}">
               <fmt:message key="newsletter.publication.tabs.subscribers"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}&method=termList">
               <fmt:message key="newsletter.publication.tabs.terms"/>
            </a>
         </div>
      </div>
   </div>
</div>

<div class="editor">
   <div style="padding-left:10px;">
   <p>
      <a href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=false&forward=newslettermanage" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat">
         <fmt:message key="site.newsletterpublication.new.blank"/>
      </a>
   </p>
   <p>
      <a href="../newsletter/NewsletterPublicationCreate.do?parent=${requestScope.newsletterId}&copycontent=true&forward=newslettermanage" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat">
         <fmt:message key="site.newsletterpublication.new.withcontent"/>
      </a>
   </p>
   </div>
   <div style="padding-left:10px;">
   <html:html>
      <html:form action="editors/newsletter/NewsletterPublicationManagement.do">
         <input type="hidden" name="searchForwardType" value="statistics"/>
         <%@include file="report_publication_search.jsp"%>
      </html:form>
      </html:html>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
   <div class="body">
      <mm:import externid="results" jspvar="nodeList" vartype="List" />
      <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer"></mm:import>
      <mm:import externid="offset" jspvar="offset" vartype="Integer"/>
      <c:if test="${resultCount > 0}">
         <%@include file="../../repository/searchpages.jsp" %>
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <thead>
               <th width="15%">&nbsp;</th>
               <th width="20%"><fmt:message key="newsletter.publication.result.title"/></th>
               <th width="20%"><fmt:message key="newsletter.publication.result.subject"/></th>
               <th width="15%"><fmt:message key="newsletter.publication.result.lastmodifier"/></th>
               <th width="30%"><fmt:message key="newsletter.publication.result.modificationdate"/></th>
            </thead>
            <c:forEach items="${results}" var="result">
               <tr>
                  <td width="15%">
                     <a href="../site/NavigatorPanel.do?nodeId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit.gif'/>" width="16" height="16"></a>
                     <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&forward=newslettermanage&parent=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16"></a>
                     <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&forward=newslettermanage&parent=${requestScope.newsletterId}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16"></a>
                     <a href="../newsletter/NewsletterPublicationTest.do?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_go.png'/>" width="16" height="16"></a>
                     <a href="../newsletter/NewsletterPublicationPublish.do?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/type/email_error.png'/>" width="16" height="16"></a>
                     <a href="../usermanagement/pagerights.jsp?number=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/rights.png'/>" width="16" height="16"></a>
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