<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
#set( $dollar = "$" )
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
	<mm:node number="${elementId}">					
      <cmsc:renderURL page="${page}" window="${window}" var="renderUrl">
         <cmsc:param name="elementId" value="${elementId}" />
      </cmsc:renderURL>
      <c:choose>
         <c:when test="${dollar}{not empty showExpanded and elementIndex lt showExpanded}">

            <mm:relatednodes max="1" type="images" role="imagerel" searchdirs="destination">
               <div class="photowrapper left"><a href="${renderUrl}"><cmsc-bm:image width="220" popup="false" /></a></div>
	             
               <%-- IE and FF treat the columns differently when there's an image floating around --%>
               <c:set var="textStyle" value="width: 261px; padding-left: 20px;" />              
            </mm:relatednodes>

            <div class="item" style="${textStyle}"><h2><a href="${renderUrl}">${elementTitle}</a></h2>
               <div class="eventinfo"><mm:field name="subtitle" /></div>
               <%-- <div class="hilite">&quot;Hi-light text&quot;</div> --%>
               <p><mm:field jspvar="intro" name="intro" escape="none" /> 
               <c:if test="${dollar}{fn:length(intro) == 0}">
                  <mm:field jspvar="body" name="body" escape="none" write="false"/>
                  <cmsc:removehtml var="cleanbody" maxlength="300" html="${body}"/>
                  ${cleanbody}
               </c:if>
               <a href="${renderUrl}" class="readon"><fmt:message key="view.readon" /><img alt="" src="<cmsc:staticurl page='/gfx/arrow_link.gif'/>" /></a></p>
            </div>
            <div class="clear"></div>
            <div class="divider"></div>
         </c:when>
         <c:otherwise>
            <li><div class="date"><mm:field name="publishdate"><mm:time format="dd-MM-yyyy" /></mm:field></div><span class="content"><a href="${renderUrl}"><c:out value="${elementTitle}"/></a></span><div class="clear"></div></li>
         </c:otherwise>
      </c:choose>
	</mm:node>
</mm:cloud>
</mm:content>