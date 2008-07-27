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
      <br/>
      <div style="padding-left:10px;">
         <html:form action="editors/newsletter/NewsletterPublicationStatisticSearch.do">
            <input type="hidden" name="method" value="searchPublicationStatistic"/>
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
                  <th width="5%">&nbsp;</th>
                  <th width="15%">
                     <a href="javascript:orderBy('title');" >
                        <fmt:message key="newsletter.publication.result.publication"/>
                     </a>
                  </th>
                  <th width="15%">
                     <a href="javascript:orderBy('publishdate');" >
                     <fmt:message key="newsletter.publication.result.sentat"/>
                     </a>
                  </th>
                  <th width="15%">
                     <a href="javascript:orderBy('subscriptions');" >
                        <fmt:message key="newsletter.publication.result.subscriptions"/>
                     </a>
                  </th>
                  <th width="15%">
                     <fmt:message key="newsletter.publication.result.sentsuccess"/>
                  </th>
                  <th width="35%">
                     <a href="javascript:orderBy('bounced');" >
                        <fmt:message key="newsletter.publication.result.bounced"/>
                     </a>
                  </th>
               </thead>
               <c:forEach items="${results}" var="result">
                  <tr>
                     <td width="5%">
                        <a href="../newsletter/NewsletterPublicationDelete.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=newsletterstatistics">
                           <img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='site.newsletterpublication.remove'/>"/>
                        </a>
                     </td>
                     <td width="15%" >
                        <a href="../newsletter/NewsletterPublicationEdit.do?number=${result.id}&parent=${requestScope.newsletterId}&forward=newsletterstatistics" title="<fmt:message key='site.newsletterpublication.edit'/>">
                           ${result.title}
                        </a>
                     </td>
                     <td width="15%" >${result.sendtime}</td>
                     <td width="15%" >${result.subscriptions}</td>
                     <td width="15%" >${result.sendsuccessful}</td>
                     <td width="35%">${result.bounced}</td>
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
