<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
  <mm:node number="${elementId}">
    <cmsc:renderURL page="${page}" window="${window}" var="renderUrl" elementId="${elementId}" />

    <li><a href="${renderUrl}"><c:out value="${elementTitle}"/></a></li>
  </mm:node>
</mm:cloud>
</mm:content>