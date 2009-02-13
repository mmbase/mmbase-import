<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
  <mm:node number="${elementId}">    
    <cmsc:renderURL page="${page}" window="${window}" var="renderUrl">
      <cmsc:param name="elementId" value="${elementId}" />
    </cmsc:renderURL>
    <h2><a href="${renderUrl}">${elementTitle}</a></h2>
    <p>
      <mm:field jspvar="question" name="question" escape="none" />                               
      <a href="${renderUrl}" class="readon">
        <fmt:message key="view.readon" />
        <img alt="" src="<cmsc:staticurl page='/gfx/arrow_link.gif'/>" />
      </a>
    </p>                
  </mm:node>
</mm:cloud>
</mm:content>