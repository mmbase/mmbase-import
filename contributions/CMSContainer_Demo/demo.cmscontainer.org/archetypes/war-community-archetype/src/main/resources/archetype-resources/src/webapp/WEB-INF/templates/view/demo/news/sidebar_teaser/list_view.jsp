<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
#set( $dollar = "$" )
<mm:content type="text/html" encoding="UTF-8">
  <mm:cloud method="asis">
    <mm:node number="${elementId}"> 

      <cmsc:renderURL page="${page}" window="${window}" elementId="${elementId}" var="renderUrl">
      </cmsc:renderURL>
  
      <div class="block">
        <div class="content">
     
          <a href="${renderUrl}">
            <mm:relatednodes type="images" role="imagerel" searchdir="destination" max="1">
              <cmsc-bm:image styleClass="photo" width="170" />
            </mm:relatednodes>
          </a>
  
          <div class="item">
            <h2><a href="${renderUrl}">${elementTitle}</a></h2>
            <div class="whiteline"></div>
     
            <mm:field jspvar="intro" name="intro" escape="none" write="false"/>                       
            <c:choose>
               <c:when test="${dollar}{fn:length(intro) > 100}">
                  ${dollar}{fn:substring(intro, 0, 100)}...
               </c:when>
               <c:otherwise>
                  ${intro}
               </c:otherwise>
            </c:choose>                           
  
            <c:if test="${dollar}{fn:length(intro) == 0}">
              <mm:field jspvar="body" name="body" escape="none" write="false"/>
              <cmsc:removehtml var="cleanbody" maxlength="100" html="${body}"/>
              
               <c:choose>
                  <c:when test="${dollar}{fn:length(cleanbody) > 100}">
                     ${dollar}{fn:substring(cleanbody, 0, 100)}... 
                  </c:when>
                  <c:otherwise>
                     ${cleanbody}
                  </c:otherwise>
               </c:choose>  
               
            </c:if>  
  
            <div>
              <a href="${renderUrl}" class="readon">
                <fmt:message key="view.readon" />
                <img alt="" src="<cmsc:staticurl page='/gfx/arrow_link.gif'/>" />
              </a>
            </div>
                 
          </div>
  
        </div>
      </div>
      <div class="divider3"></div>
  
    </mm:node>
  </mm:cloud>
</mm:content>