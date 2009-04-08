<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="community_login">
   <h3><fmt:message key="view.logout" /></h3>
   
   <fmt:message key="view.logged_in_as" />&nbsp;${pageContext.request.remoteUser}.
   
   <a href="<cmsc:actionURL><cmsc:param name="action" value="logout"/></cmsc:actionURL>">
     <fmt:message key="view.logout" />
   </a>

</div>