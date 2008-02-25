<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<h3><fmt:message key="view.login" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL><cmsc:param name="action" value="login"/></cmsc:actionURL>" 
      target="_parent" 
      method="post">

<table>
  <tr class="inputrow">
    <td><fmt:message key="view.username" /></td>
    <td><input type="text" name="j_username"/></td>
  </tr>
  <tr class="inputrow">
    <td><fmt:message key="view.password" /></td>
    <td><input type="password" name="j_password"/></td>
  </tr>
  <tr>
    <td></td>
    <td id="Submit" align="right">
      <input type="submit" value="<fmt:message key="view.submit" />" />
    </td>
  </tr>
</table>

</form>
