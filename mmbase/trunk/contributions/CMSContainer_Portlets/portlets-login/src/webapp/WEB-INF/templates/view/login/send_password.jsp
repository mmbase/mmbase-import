<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h3><fmt:message key="view.account.sendpassword" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL><cmsc:param name="action" value="send_password"/></cmsc:actionURL>" 
      method="post">
<table>
<c:if test="${!empty sendMessage && sendMessage ne 'send'}">
  <tr class="inputrow" >
     <td colspan="2">
      <fmt:message key="${sendMessage}" />
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
    <td id="Submit" align="right">
      <input type="submit" value="<fmt:message key="view.send_password.submit" />" />
    </td>
  </tr>
</c:if>
</table>
</form>
