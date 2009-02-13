<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>
#set( $dollar = "$" )
<cmsc:location var="cur" sitevar="site" />
<!-- linklist -->
<div class="linkcontainer">
<div class="content">
<div class="linklist">
<c:if test="${dollar}{not empty portletTitle}">
  <h2>${portletTitle}</h2>
</c:if>
  <ul>