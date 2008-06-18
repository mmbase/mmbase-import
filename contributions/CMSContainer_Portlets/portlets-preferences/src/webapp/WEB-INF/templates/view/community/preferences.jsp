<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<fmt:setBundle basename="portlets-prefercences" scope="request" />
<c:if test="${preferenceFormUrls != null && fn:length(preferenceFormUrls) >0}">
   <div class="heading">
      <h3><fmt:message key="preference.preference.title"/></h3>
   </div>
     <div class="editor" style="height:500px">
        <div class="body">
           <form method="POST" name="<portlet:namespace />form_preference"
             action="<cmsc:actionURL/>">
		      	<input type="hidden" name="action" value="preference">
               <table border="0">
                  <tr class="listheader">
                     <th><fmt:message key="community.preference.module" /></th>
                     <th><fmt:message key="community.preference.key" /></th>
                     <th><fmt:message key="community.preference.value" /></th>
                  </tr>
                  <c:forEach var="url" items="${preferenceFormUrls}">
                     <c:import url ="${url}" />
                  </c:forEach>
                  <tr>
                     <td style="width: 150px"><input type="submit" name="submitButton" onclick="javascript:document.forms['<portlet:namespace />form_preference'].submit()" 
                              value="<fmt:message key="view.submit" />"/></td>
                     <td>
                     </td>
                  </tr>
             </table>
         </form>
	</div>
   </c:if>


