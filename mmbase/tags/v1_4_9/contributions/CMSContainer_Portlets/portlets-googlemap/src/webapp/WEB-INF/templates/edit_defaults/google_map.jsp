<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form name="<portlet:namespace />form" method="post" target="_parent"
   action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

   <table class="editcontent">
      <tr>
         <td><fmt:message key="edit_defaults.address" />:</td>
         <td><input type="text" name="address" value="${address}" /></td>
      </tr>
      <tr>
         <td><fmt:message key="edit_defaults.info" />:</td>
         <td><input type="text" name="info" value="${info}" /></td>
      </tr>
      </tr>
      <tr>
         <td><fmt:message key="edit_defaults.key" />:</td>
         <td><input type="text" name="key" value="${key}" /></td>
      </tr>
      <tr>
         <td><fmt:message key="edit_defaults.height" />:</td>
         <td><cmsc:text var="height" /></td>
      </tr>
      <tr>
         <td><fmt:message key="edit_defaults.width" />:</td>
         <td><cmsc:text var="width" /></td>
      </tr>
   
      <tr>
         <td><a href="javascript:document.forms['<portlet:namespace />form'].submit()"
            class="button"> <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt="" />
         <fmt:message key="edit_defaults.save"/></a></td>
      </tr>
   
   </table>
</form>

</div>