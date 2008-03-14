<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<h3><fmt:message key="view.send_password" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL><cmsc:param name="action" value="send_password"/></cmsc:actionURL>" 
      method="post">

<table>
<c:if test="${!empty errormessage}">
  <tr class="inputrow" style="color: red;" >
  <td colspan="2">
	<fmt:message key="${errormessage}" />
  </td>
  </tr>
</c:if>

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
</table>

</form>
