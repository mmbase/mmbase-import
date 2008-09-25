<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://www.luceus.com/taglib" prefix="lm"%>

<div class="portlet-config-canvas">
<h3><fmt:message key="edit_defaults.title" /></h3>

<form method="POST" name="<portlet:namespace />form" action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>" target="_parent">

<table class="editcontent">
   <tr>
      <td><fmt:message key="edit_defaults.view" />:</td>
      <td>
         <cmsc:select var="view">
            <c:forEach var="v" items="${views}">
               <cmsc:option value="${v.id}" name="${v.title}" />
            </c:forEach>
         </cmsc:select><br />
      </td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.elementsperpage" />:</td>
      <td><cmsc:select var="elementsPerPage">
         <cmsc:option value="" message="edit_defaults.unlimited" /> 
         <cmsc:option value="5" />
         <cmsc:option value="10" />
         <cmsc:option value="15" />
         <cmsc:option value="20" />
         <cmsc:option value="25" />
         <cmsc:option value="50" />
      </cmsc:select></td>
   </tr>
   <tr>
      <td><fmt:message key="edit_defaults.numberofpages" />:</td>
      <td><cmsc:select var="showPages">
         <cmsc:option value="" message="edit_defaults.unlimited" /> 
         <cmsc:option value="5" />
         <cmsc:option value="10" />
         <cmsc:option value="15" />
         <cmsc:option value="20" />
      </cmsc:select></td>
   </tr>

   <tr>
      <td><fmt:message key="edit_defaults.maxelements" />:</td>
      <td>
         <input type="text" name="maxElements" value="${maxElements}" />
      </td>
   </tr>

	<tr>
		<td><fmt:message key="edit_defaults.pagesindex" />:</td>
		<td><cmsc:select var="pagesIndex">
			<cmsc:option value="center" message="edit_defaults.pagesindex.center" />
			<cmsc:option value="forward" message="edit_defaults.pagesindex.forward" />
			<cmsc:option value="half-full" message="edit_defaults.pagesindex.half-full" />
		</cmsc:select></td>
	</tr>

   <tr>
      <td><fmt:message key="edit_defaults.indexname" />:</td>
      <td><lm:listindexes var="indexes"/>
         <cmsc:select var="indexName">
            <c:forEach var="idx" items="${indexes}">
               <cmsc:option value="${idx}" name="${idx}" />
            </c:forEach>
         </cmsc:select>
      </td>
   </tr>
   
   <tr>
      <td colspan="2">
			<a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
				<img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/> <fmt:message key="edit_defaults.save" /></a>
      </td>
   </tr>
</table>

</div>
