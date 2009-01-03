<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cmsc:location var="cur" sitevar="site" />
<cmsc:path var="listPath" />
<cmsc:list-navigations var="pages" origin="${listPath[1]}" />

<div id="navleft">
   <c:forEach var="page" items="${pages}">
      <c:choose>
         <c:when test="${listPath[2].id eq page.id and fn:length(listPath) eq 3}">        
            <div class="selected">
               <a href="<cmsc:link dest="${page.id}"/>" title="<c:out value='${page.description}'/>">
                  <c:out value='${page.title}' />
               </a>
            </div>
         </c:when>
         <c:otherwise>
            <div class="menuitem">
               <a href="<cmsc:link dest="${page.id}"/>" title="<c:out value='${page.description}'/>">
                  <c:out value='${page.title}'/>
               </a>
            </div>
         </c:otherwise>
      </c:choose>

      <c:if test="${fn:length(listPath) > 2 and (listPath[2] eq page or listPath[3] eq page)}">
         <cmsc:list-navigations var="subchannels" origin="${page}"/>
         <c:if test="${not empty subchannels}">
            <c:forEach var="subchan" items="${subchannels}">
               <c:choose>
                  <c:when test="${listPath[3].id eq subchan.id}">             
                     <div class="selected2">
                        <a href="<cmsc:link dest="${subchan.id}"/>" title="<c:out value='${subchan.description}'/>">
                           <c:out value='${subchan.title}'/>
                        </a>
                     </div>
                  </c:when>
                  <c:otherwise>
                     <div class="sub">
                        <a href="<cmsc:link dest="${subchan.id}"/>" title="<c:out value='${subchan.description}'/>">
                           <c:out value='${subchan.title}'/>
                        </a>
                     </div>
                  </c:otherwise>
               </c:choose>
            </c:forEach>
         </c:if>
      </c:if>

   </c:forEach>
</div>
