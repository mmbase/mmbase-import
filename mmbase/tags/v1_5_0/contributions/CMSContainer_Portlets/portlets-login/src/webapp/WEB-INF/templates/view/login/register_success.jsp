<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:if test="${empty active}">
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
</c:if>
<c:if test="${!empty active}">
<br/>
  <table>
     <tr class="inputrow">
       <td>
         <c:if test="${active == 'success'}">
         <fmt:message key="register.active.success" />
         </c:if>
         <c:if test="${active == 'failure'}">
         <fmt:message key="register.active.failure" />
         </c:if>
         <c:if test="${active == 'actived'}">
         <fmt:message key="register.active.alreadyactivated" />
         </c:if>
       </td>
     </tr>
   </table>
</c:if>
