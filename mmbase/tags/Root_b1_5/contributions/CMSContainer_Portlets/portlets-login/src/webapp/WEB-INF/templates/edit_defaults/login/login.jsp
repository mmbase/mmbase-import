<%@include file="/WEB-INF/templates/portletglobals.jsp" %>

<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form 
  method="post" 
  name="<portlet:namespace />form" 
  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" 
  target="_parent"
>

<table class="editcontent">
   <c:if test="${fn:length(views) gt 0}">
      <tr>
         <td><fmt:message key="edit_defaults.view" />:</td>
         <td>
            <cmsc:select var="view">
               <c:forEach var="v" items="${views}">
                  <cmsc:option value="${v.id}" name="${v.title}" />
               </c:forEach>
            </cmsc:select>
         </td>
      </tr>
   </c:if>
   <tr>
      <td colspan="2">
         <input type="submit" value="<fmt:message key="edit_defaults.save" />" class="button" />
      </td>
   </tr>
</table>
</div>