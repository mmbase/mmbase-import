<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h3><fmt:message key="view.account.sendpassword" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL><cmsc:param name="action" value="send_password"/></cmsc:actionURL>" 
      method="post">
<c:if test="${sendMessage eq 'send'}">
   <p><fmt:message key="view.send_password.entertext"/></p>
</c:if>
<table>
<c:if test="${!empty sendMessage && sendMessage ne 'send'}">
  <tr class="inputrow">
     <td><fmt:message key="${sendMessage}" /></td>
     <td id="Submit">
        <a href="<cmsc:renderURL page="${page}"/>"/><fmt:message key="view.send_password.back"/></a>
     </td>
  </tr>
</c:if>
<c:if test="${sendMessage eq 'send'}">
  <tr class="inputrow">
    <td><fmt:message key="view.send_password.username" /></td>
    <td><input type="text" name="username"/></td>
  </tr>
  <tr>
    <td></td>
    <td id="Submit">
      <input type="submit" value="<fmt:message key="view.send_password.submit" />" />
    </td>
  </tr>
</c:if>
</table>
</form>
