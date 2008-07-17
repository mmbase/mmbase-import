<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<!-- start itemHeader -->
<c:if test="${not empty showExpanded and elementLast and elementIndex ge showExpanded}">
  <div class="list">
    <ul>
</c:if>