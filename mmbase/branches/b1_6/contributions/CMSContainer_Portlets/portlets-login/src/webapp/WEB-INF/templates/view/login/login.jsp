<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<fmt:setBundle basename="com.finalist.cmsc.portlets.LoginPortlet" scope="request"/>
<h3><fmt:message key="view.login" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL><cmsc:param name="action" value="login"/></cmsc:actionURL>" 
      method="post">
<input type="hidden" name="send_password" id="send_password"/>

<c:if test="${!empty errormessage}">
<span class="inputrow" style="color: red;" >
   <fmt:message key="${errormessage}" />
</span>
</c:if>

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
    <td id="Submit" align="right" colspan="2">
      <input type="submit" value="<fmt:message key="view.submit" />" />
      <input type="button" value="<fmt:message key="view.send_password" />" onclick="javascript:this.form['send_password'].value='send';this.form.submit()"/>
    </td>
  </tr>
</table>

</form>
