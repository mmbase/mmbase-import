<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
#set( $dollar = "$" )
<!-- start itemHeader -->
<c:if test="${dollar}{not empty showExpanded and elementLast and elementIndex ge showExpanded}">
  <div class="list">
    <ul>
</c:if>