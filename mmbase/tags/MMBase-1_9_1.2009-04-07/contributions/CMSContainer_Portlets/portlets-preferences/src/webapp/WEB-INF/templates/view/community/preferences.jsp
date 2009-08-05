<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<c:if test="${preferenceFormUrls != null && fn:length(preferenceFormUrls) >0}">
   
   <cmsc:portletmode name="edit">
      <%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
   </cmsc:portletmode>

   <div class="content">      
   <h2><fmt:message key="preference.preference.title" /></h2>

   <form method="POST" name="<portlet:namespace />form_preference" action="<cmsc:actionURL/>">
      <input type="hidden" name="action" value="preference">
      <table>
         <tr class="listheader">
            <th><fmt:message key="community.preference.module" /></th>
            <th><fmt:message key="community.preference.key" /></th>
            <th><fmt:message key="community.preference.value" /></th>
         </tr>
         <c:forEach var="url" items="${preferenceFormUrls}">
            <c:import url="${url}" />
         </c:forEach>
      </table>
      <p>
      <input type="submit" name="submitButton"
               onclick="javascript:document.forms['<portlet:namespace />form_preference'].submit()"
               value="<fmt:message key="view.submit" />" />
      </p>
   </form>
   </div>
   
   <cmsc:portletmode name="edit">
      <%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
   </cmsc:portletmode> 
</c:if>