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
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}&searchForwardType=statistics">
               <fmt:message key="newsletter.publication.tabs.statistics"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab_active">
      <div class="body">
         <div>
            <a href="#">
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
      <a href="#" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat">
         <fmt:message key="newsletter.publication.link.newsubscriber"/>
      </a>
   </p>
   <p>
      <a href="../../../community/userAddInitAction.do" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" target="_blank">
         <fmt:message key="newsletter.publication.link.newuser"/>
      </a>
   </p>
   </div>

   <div style="padding-left:10px;">
      <html:form action="editors/newsletter/NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}">
         <input type="hidden" name="action" value="subScriberSearch"/>
         <table width="50%" border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td width="25%"><fmt:message key="newsletter.publication.result.fullname"/></td>
               <td width="75%">
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
      <div class="ruler_green"><div><fmt:message key="newsletter.publication.result"/></div></div>
      <div class="body">
      <mm:import externid="results" jspvar="nodeList" vartype="List" />
      <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer"></mm:import>
      <c:if test="${resultCount > 0}">
         <%@include file="../../repository/searchpages.jsp" %>
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <thead>
                  <th width="5%">&nbsp;</th>
                  <th width="20%"><a href="#"><fmt:message key="newsletter.publication.result.fullname"/></a></th>
                  <th width="20%"><a href="#"><fmt:message key="newsletter.publication.result.username"/></a></th>
                  <th width="20%"><a href="#"><fmt:message key="newsletter.publication.result.email"/></a></th>
                  <th width="20%"><fmt:message key="newsletter.publication.result.newsletter"/></th>
                  <th width="15%"><fmt:message key="newsletter.publication.result.terms"/></th>
            </thead>
            <c:forEach items="${results}" var="result">
            <tr>
                  <td>
                  <a href="#"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16"></a>
                  <a href="#"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16"></a>
                  </td>
                  <td>${result.fullname}</td>
                  <td>${result.username}</td>
                  <td>${result.email}</td>
                  <td>${result.newsletters}</td>
                  <td>${result.terms}</td>
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
</div>