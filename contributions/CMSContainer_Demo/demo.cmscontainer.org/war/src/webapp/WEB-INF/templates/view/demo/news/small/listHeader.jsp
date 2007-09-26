<%@ include file="/WEB-INF/templates/portletglobals.jsp" %>
<cmsc:location var="cur" sitevar="site" />
<!-- linklist -->
<div class="linkcontainer">
<div class="content">
<div class="linklist">
<c:if test="${not empty portletTitle}">
  <h2>${portletTitle}</h2>
</c:if>
  <ul>