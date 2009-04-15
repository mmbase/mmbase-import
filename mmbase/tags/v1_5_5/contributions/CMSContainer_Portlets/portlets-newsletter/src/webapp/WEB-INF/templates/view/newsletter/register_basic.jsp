<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h2><fmt:message key="register.title" /></h2>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">
   <c:if test="${!empty errormessages['defaultmessages']}">
   <p><fmt:message key="${errormessages['defaultmessages']}" /></p>    
   </c:if>
   <div id="user">
      <input type="hidden" name="page" value="${page}" />
      <table class="formcontent">     
         <tr>
            <td class="fieldname"><fmt:message key="register.email" /></td>
            <td><input type="text" name="email" size='30' value="${email}" /><font color="red">*</font></td>
         </tr>
         <c:if test="${!empty errormessages['email']}">
         <tr>
            <td colspan="2"><fmt:message key="${errormessages['email']}" /></td>
         </tr>
         </c:if>
         <tr>
            <td></td>
            <td><input type="submit" value="<fmt:message key="register.submit" />" /></td>
          </tr>
        </table>
   </div>
</form>
