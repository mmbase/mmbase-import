<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
   <form method="POST" name="<portlet:namespace />form_profile"
      action="<cmsc:actionURL/>">
    <input type="hidden" name="action" value="profile">
    <div class="heading">
      <h3><fmt:message key="preference.profile.title"/></h3>
   </div>
		<div id="user">
		<table class="formcontent">
       	<tr>
            <td class="fieldname" width='180'><fmt:message key="view.user" /></td>
				<td>
              <c:out value="${profile.account}"/><input type="hidden" name="account" id="account" value="${profile.account}">
				</td>
			</tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.firstname" /></td>
            <td><input type="text" name="firstName" id="firstName" value="${profile.firstName}">
            <span class="notvalid"></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.prefix" /></td>
            <td><input type="text" name="prefix" id="prefix" size="15" value="${profile.prefix}">
            <span class="notvalid"></span>
            </td>
         </tr>         
         <tr>
            <td class="fieldname"><fmt:message key="view.surname" /></td>
            <td><input type="text" name="lastName" id="lastName" size="30" value="${profile.lastName}">
            <span class="notvalid"></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.email" /></td>
            <td><input type="text" name="email" id="email" size="30" value="${profile.email}">
            <span class="notvalid"></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.password" /></td>
            <td>
               <input type="password" name="passwordText" id="passwordText" size="15" maxlength="15">
               <span class="notvalid"></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname" nowrap><fmt:message key="view.confirmpassword" /></td>
            <td><input type="password" name="passwordConfirmation" id="passwordConfirmation" size="15" maxlength="15">
                <span class="notvalid"></span>
            </td>
         </tr>           
		</table>
		</div>
      <c:if test="${!empty errors}">
         <label> <font color="red">
         <c:forEach var="error" items="${errors}">
             <fmt:message key="${error}" /><br/>
          </c:forEach>
        </font color="red"></label>
      </c:if>
		<br />
		<div style="padding: 5px;">
            <input type="submit" style="width:90" name="save" onclick="javascript:document.forms['<portlet:namespace />form_profile'].submit()"  value="<fmt:message key="view.submit"/>">
		</div>
	</form>