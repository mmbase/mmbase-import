<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>
<script src="../repository/search.js" type="text/javascript"></script>
<div class="tabs">
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}&searchForwardType=edit">
               <fmt:message key="newsletter.publication.tabs.edit"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#">
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
   <br/>
   <div style="padding-left:10px;">
   <html:html>
      <html:form action="editors/newsletter/NewsletterPublicationManagement.do">
         <input type="hidden" name="searchForwardType" value="edit"/>
         <%@include file="report_publication_search.jsp"%>
      </html:form>
      </html:html>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
   <div class="body">
      <mm:import externid="results" jspvar="nodeList" vartype="List" />
      <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">5</mm:import>
      <mm:import externid="offset" jspvar="offset" vartype="Integer"/>
      <c:if test="${resultCount > 0}">
         <%@include file="../../repository/searchpages.jsp" %>
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <thead>
               <th width="5%">&nbsp;</th>
               <th width="15%"><fmt:message key="newsletter.publication.result.publication"/></th>
               <th width="15%"><fmt:message key="newsletter.publication.result.sentat"/></th>
               <th width="15%"><fmt:message key="newsletter.publication.result.subscriptions"/></th>
               <th width="15%"><fmt:message key="newsletter.publication.result.sentsuccess"/></th>
               <th width="35%"><fmt:message key="newsletter.publication.result.bounced"/></th>
            </thead>
            <c:forEach items="${results}" var="result">
               <tr>
                  <td width="5%">
                     <a href="#"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16"></a>
                  </td>
                  <td width="15%" >${result.title}</td>
                  <td width="15%" >${result.sentTime}</td>
                  <td width="15%" >${result.subscriptionCount}</td>
                  <td width="15%" >${result.sendsuccessCount}</td>
                  <td width="35%">${result.bounced}</td>
               </tr>
            </c:forEach>
         </table>
      </c:if>
      <c:if test="${resultCount == 0 && param.title != null}">
         <fmt:message key="newsletter.publication.noresult"/>
      </c:if>
      <c:if test="${resultCount > 0}">
         <%@include file="../../repository/searchpages.jsp" %>
      </c:if>
   </div>
</div>