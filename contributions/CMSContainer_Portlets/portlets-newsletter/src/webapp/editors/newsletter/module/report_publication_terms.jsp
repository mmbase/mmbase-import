<%@include file="globals.jsp" 
%><%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" 
%><%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html>
   <cmscedit:head title="index.title">
      <script src="../repository/search.js" type="text/javascript"></script>
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
      <p>
         <a href="module/NewsletterTermAction.do?method=list&newsletterId=${requestScope.newsletterId}" style=" padding-left:20px; background: url(<cmsc:staticurl page='/editors/gfx/icons/type/email_add.png'/>) left center no-repeat" title="<fmt:message key='newsletter.publication.search.linkterm'/>">
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
                  <td>
                     <input type="submit" value="Search"/>
                     <input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
                     <input type="hidden" name="order" value="${requestScope.order}"/>
                     <input type="hidden" name="direction" value="${requestScope.direction}"/>
                     <input type="hidden" name="offset" value="0"/>
                  </td>
               </tr>
            </table>
         </form>
         <br/>
      </div>
   </div>

   <div class="editor" style="height:455px">
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
                  <th scope="95%"><a href="javascript:orderBy('name');"><fmt:message key="newsletter.publication.search.link.name"/></a></th>
               </thead>
               <c:forEach items="${results}" var="result">
               <tr>
                  <td><a href="../../editors/newsletter/NewsletterTermDelete.do?newsletterId=${requestScope.newsletterId}&termId=${result.id}"><img src="<cmsc:staticurl page='/editors/gfx/icons/delete.png'/>" width="16" height="16" title="<fmt:message key='newsletter.icons.title.term.unlink'/>"></a></td>
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
   </body>
</html>
</mm:content>
