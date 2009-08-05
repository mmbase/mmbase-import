<%@ tag body-content="scriptless" %>
<%@ attribute name="key" rtexprvalue="true" required="false" %>
<%@ attribute name="title" rtexprvalue="true" required="false" %>
<%@ attribute name="active" rtexprvalue="true" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="class" value="${active ? 'tab_active' : 'tab'}"/>

<jsp:doBody var="action"/>
<div class="${class}">
   <div class="body">
      <div>
         <c:choose>
            <c:when test="${empty title}">
               <a href="${action}"><fmt:message key="${key}"/></a>
            </c:when>
            <c:otherwise>
               <a href="${action}">${title}</a>
            </c:otherwise>
         </c:choose>
      </div>
   </div>
</div>

 