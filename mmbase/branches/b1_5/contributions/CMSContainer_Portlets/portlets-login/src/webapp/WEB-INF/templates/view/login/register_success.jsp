<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<h3><fmt:message key="register.success" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">

<table>
  <tr class="inputrow">
    <td><fmt:message key="register.success.information" />
    </td>
  </tr>
</table>

</form>
