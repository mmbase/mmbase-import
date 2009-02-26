<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h3><fmt:message key="register.title" /></h3>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">
   <c:if test="${!empty errormessage}">
   <table>
     <tr class="inputrow" style="color: red;" >
        <td colspan="2">
          <fmt:message key="${errormessage}" />
        </td>
     </tr>
   <table>
   </c:if>
   <div id="user">
      <input type="hidden" name="page" value="${page}" />
      <table class="formcontent">     
         <tr>
            <td class="fieldname"><fmt:message key="register.email" />
            </td>
            <td><input type="text" name="email" size='30' /><font color="red">*</font>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="register.firstName" /></td>
            <td><input type="text" name="firstName" size="30" />
            </td>
         </tr> 
         <tr>
            <td class="fieldname"><fmt:message key="register.infix" /></td>
            <td><input type="text" name="infix" size="15" />
            </td>
         </tr>         
         <tr>
            <td class="fieldname"><fmt:message key="register.lastname" /></td>
            <td><input type="text" name="lastName" size='30' />

            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="register.password" /></td>
            <td>
               <input type="password" name="passwordText" size="15" maxlength="15" /><font color="red">*</font>
   
            </td>
         </tr>
         <tr>
            <td class="fieldname" nowrap><fmt:message key="register.confirmpassword" /></td>
            <td>
               <input type="password" name="passwordConfirmation" size="15" maxlength="15" /><font color="red">*</font>
             </td>
         </tr>
         <tr>
          <td></td>
          <td id="Submit">
            <input type="submit" value="<fmt:message key="register.submit" />" />
          </td>
          </tr>
        </table>
      </div>

         <c:if test="${!empty errormessages}">
         <label> <font color="red">
         <c:forEach var="error" items="${errormessages}">
             <fmt:message key="${error}" /><br/>
          </c:forEach>
        </font></label>
      </c:if>
</form>
