<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.register.title" /></h3>

<form 
  method="post" 
  name="<portlet:namespace />form" 
  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" 
  target="_parent"
>

<table class="editcontent">
   <tr>
      <td colspan="2"><h4><fmt:message key="edit_defaults.register.group" /></h4></td>
   </tr>
   <tr>
      <td ><fmt:message key="edit_defaults.register.defaultgroup" />:</td>
      <td>
         <cmsc:select var="groupName">
            <cmsc:option value="nogroup" message="edit_defaults.register.nogroup" />
            <c:forEach items="${allGroupNames}" var="group">
            <cmsc:option value="${group}">${group}</cmsc:option>
            </c:forEach>
         </cmsc:select>
      </td>
   </tr>
   <tr>
      <td colspan="2"><h4><fmt:message key="edit_defaults.register.subject" /></h4></td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.register.emailfromname" />:</td>
      <td>
         <input type="text" name="emailFromName" value="${fn:escapeXml(emailFromName)}" />
      </td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.register.emailfromemail" />:</td>
      <td>
         <input type="text" name="emailFromEmail" value="${fn:escapeXml(emailFromEmail)}" />
      </td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.register.emailsubject" />:</td>
      <td>
         <input type="text" name="emailSubject" value="${fn:escapeXml(emailSubject)}" />
      </td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.register.emailtext" />:</td>
      <td>
         <textarea name="emailText" rows="5" cols="20"><c:out value="${emailText}" /></textarea>
      </td>
   </tr>
   <tr>
      <td colspan="2">
         <a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
            <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/>
            <fmt:message key="edit_defaults.save" />
         </a>
      </td>
   </tr>
   
</table>
</div>