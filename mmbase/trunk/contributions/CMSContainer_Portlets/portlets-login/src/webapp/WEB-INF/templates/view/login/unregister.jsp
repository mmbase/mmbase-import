<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<h2><fmt:message key="unregister.title" /></h2>
<script type="text/javascript">
    //<![CDATA[
   function emailvalidate() {
      if(confirm('<fmt:message key="${unregister.confirm}" />')) {
         document.<portlet:namespace />form.submit();
      }
   }
    //]]>

</script>
<form name="<portlet:namespace />form" 
      action="<cmsc:actionURL/>" 
      method="post">
    <div>
      <c:choose>
         <c:when test="${empty errormessages && !empty removeSuccess}">
            <p><fmt:message key="${removeSuccess}" /></p>   
         </c:when>
         <c:otherwise>
            <table class="formcontent">     
               <tr>
                  <td class="fieldname"><fmt:message key="register.email" /></td>
                  <td><input type="text" name="registerEmail" size='30' value="${registerEmail}" /></td>
               </tr>
               <c:if test="${!empty errormessages['registerEmail']}">
               <tr>
                  <td colspan="2"><fmt:message key="${errormessages['registerEmail']}" /></td>
               </tr>
               </c:if>
                <td></td>
                <td id="Submit"><input type="button" onclick="emailvalidate()" value="<fmt:message key="register.submit" />" /></td>
                </tr>
              </table>
         </c:otherwise>
      </c:choose>
   </div>
</form>
