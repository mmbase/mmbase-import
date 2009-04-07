<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h2><fmt:message key="unregister.title" /></h2>

<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">
   <div id="user">
      <table class="formcontent">     
         <tr>
            <td class="fieldname"><fmt:message key="unregister.confirmation" /></td>
            <td><textarea name="confirmationText" rows="8" cols="25"><c:out value="${confirmationText}" /></textarea></td>
         </tr>
          <td></td>
          <td id="Submit"><input type="submit" value="<fmt:message key="register.submit" />" /></td>
          </tr>
        </table>
   </div>
</form>
