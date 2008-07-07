<%@include file="globals.jsp" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>

<cmscedit:head title="reactions.title">
</cmscedit:head>

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
   <div class="tab">
      <div class="body">
         <div>
            <a href="NewsletterPublicationSubscriberSearch.do?newsletterId=${requestScope.newsletterId}">
               <fmt:message key="newsletter.publication.tabs.subscribers"/>
            </a>
         </div>
      </div>
   </div>
   <div class="tab_active">
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
         <fmt:message key="newsletter.publication.search.linkterm"/>
      </a>
   </p>
   </div>

   <div style="padding-left:10px;">
      <form method="post" action="NewsletterTermSearch.do?newsletterId=${requestScope.newsletterId}">
         <table width="50%" border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td><fmt:message key="newsletter.publication.search.link.name"/></td>
               <td><input type="text" name="name" size="30"/></td>
               <td><input type="submit" value="Search"/></td>
            </tr>
         </table>
      </form>
      <br/>
   </div>
</div>

<div class="editor">
   <div class="ruler_green"><div>&nbsp;<fmt:message key="newsletter.publication.result"/>&nbsp;</div></div>
   <div class="body">
      <mm:import externid="results" jspvar="nodeList" vartype="List" />
      <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
      <mm:import externid="offset" jspvar="offset" vartype="Integer"/>
      <c:if test="${resultCount > 0}">
         <%@include file="../../repository/searchpages.jsp" %>
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <thead>
               <th width="5%">&nbsp;</th>
               <th scope="95%"><fmt:message key="newsletter.publication.search.link.name"/></th>
            </thead>
            <c:forEach items="${results}" var="result">
            <tr>
               <td><a href="#"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16"></a></td>
               <td>${result.name}</td>
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