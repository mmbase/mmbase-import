<%@ tag body-content="scriptless" %>
<%@ attribute name="key" rtexprvalue="true" required="true" %>
<%@ attribute name="active" rtexprvalue="true" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="class" value="${active ? 'tab_active' : 'tab'}"/>

<jsp:doBody var="action"/>
<div class="${class}">
   <div class="body">
      <div>
         <a href="${action}"><fmt:message key="${key}"/></a>
      </div>
   </div>
</div>