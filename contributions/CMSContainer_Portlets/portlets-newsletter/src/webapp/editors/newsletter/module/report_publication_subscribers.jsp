<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
   </cmscedit:head>
   <body>
   <div class="tabs">
      <div class="tab">
         <div class="body">
            <div>
               <a href="NewsletterPublicationManagement.do?newsletterId=${requestScope.newsletterId}">
                  <fmt:message key="newsletter.publication.tabs.edit"/>
               </a>
            </div>
         </div>
      </div>
      <div class="tab">
         <div class="body">
            <div>
               <a href="NewsletterPublicationStatisticSearch.do?newsletterId=${requestScope.newsletterId}">
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
               <a href="NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}">
                  <fmt:message key="newsletter.publication.tabs.terms"/>
               </a>
            </div>
         </div>
      </div>
   </div>

   <div class="editor">
      <div style="padding-left:10px;">
      <p>
         <a href="#" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" title="<fmt:message key='newsletter.publication.link.newsubscriber'/>">
            <fmt:message key="newsletter.publication.link.newsubscriber"/>
         </a>
      </p>
      <p>
         <a href="../community/userAddInitAction.do?newsletterId=${requestScope.newsletterId}&forward=newslettersubscription" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" title="<fmt:message key='newsletter.publication.link.newuser'/>">
            <fmt:message key="newsletter.publication.link.newuser"/>
         </a>
      </p>
      </div>

      <div style="padding-left:10px;">
         <html:form action="editors/newsletter/NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}">
            <input type="hidden" name="method" value="subScriberSearch"/>
            <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
            <input type="hidden" name="order" value="${requestScope.order}"/>
            <input type="hidden" name="direction" value="${requestScope.direction}"/>
            <input type="hidden" name="offset" value="0"/>
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
         <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
         <div class="body">
         <mm:import externid="results" jspvar="nodeList" vartype="List" />
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer"></mm:import>
         <c:if test="${resultCount > 0}">
            <%@include file="../../repository/searchpages.jsp" %>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
               <thead>
                     <th width="5%">&nbsp;</th>
                     <th width="20%"><a href="javascript:orderBy('fullname');"><fmt:message key="newsletter.publication.result.fullname"/></a></th>
                     <th width="20%"><a href="javascript:orderBy('username');"><fmt:message key="newsletter.publication.result.username"/></a></th>
                     <th width="20%"><a href="javascript:orderBy('email');"><fmt:message key="newsletter.publication.result.email"/></a></th>
                     <th width="20%"><fmt:message key="newsletter.publication.result.newsletter"/></th>
                     <th width="15%"><fmt:message key="newsletter.publication.result.terms"/></th>
               </thead>
               <c:forEach items="${results}" var="result">
               <tr>
                     <td>
                     <a href="../community/userAddInitAction.do?authid=${result.id}&newsletterId=${requestScope.newsletterId}&forward=newslettersubscribers"><img src="<cmsc:staticurl page='/editors/gfx/icons/edit_defaults.png'/>" width="16" height="16"  title="<fmt:message key='newsletter.icons.title.edituser'/>"/></a>
                     <a href="NewsletterNewsletterSubscriberDelete.do?newsletterId=${requestScope.newsletterId}&authid=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.user.unsubscribe'/>"/></a>
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
   </body>
</html>
</mm:content>
