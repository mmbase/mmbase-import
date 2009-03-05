<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<h2><fmt:message key="register.title" /></h2>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">
   <c:if test="${!empty errorMessages['defaultmessages']}">
   <p><fmt:message key="${errorMessages['defaultmessages']}" /></p>    
   </c:if>
   <div id="user">
      <input type="hidden" name="page" value="${page}" />
      <table class="formcontent">     
         <tr>
            <td class="fieldname"><fmt:message key="register.email" /></td>
            <td><input type="text" name="email" size='30' /><font color="red">*</font></td>
         </tr>
         <c:if test="${!empty errorMessages['email']}">
         <tr>
            <td colspan="2"><fmt:message key="${errorMessages['email']}" /></td>
         </tr>
         </c:if>
         <tr>
            <td class="fieldname"><fmt:message key="register.firstName" /></td>
            <td><input type="text" name="firstName" size="30" /><font color="red">*</font></td>
         </tr> 
         <tr>
            <td class="fieldname"><fmt:message key="register.infix" /></td>
            <td><input type="text" name="infix" size="15" /></td>
         </tr>         
         <tr>
            <td class="fieldname"><fmt:message key="register.lastname" /></td>
            <td><input type="text" name="lastName" size='30' /><font color="red">*</font></td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="register.password" /></td>
            <td><input type="password" name="passwordText" size="15" maxlength="15" /><font color="red">*</font></td>
         </tr>
         <tr>
            <td class="fieldname" nowrap><fmt:message key="register.confirmpassword" /></td>
            <td><input type="password" name="passwordConfirmation" size="15" maxlength="15" /><font color="red">*</font></td>
         </tr>
         <c:if test="${useterms eq 'yes'}">
            <tr>
               <td><a href="<cmsc:link dest='${page}'/>"><fmt:message key="register.terms.title"/></a></td>
               <td><input type="checkbox" name="agreedToTerms"/><fmt:message key="register.terms.agree" /></td>
            </tr>
         </c:if>
         <tr>
          <td></td>
          <td id="Submit"><input type="submit" value="<fmt:message key="register.submit" />" /></td>
          </tr>
        </table>
   </div>
</form>
