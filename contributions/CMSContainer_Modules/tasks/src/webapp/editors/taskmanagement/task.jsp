<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<%@page import="com.finalist.cmsc.repository.ContentElementUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="task.pagetitle">
  <style>
	input.select { font-height: 4px;}
  </style>
</cmscedit:head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" loginpage="../login.jsp" rank='basic user'>
	<html:form action="/editors/taskmanagement/TaskAction">
		<html:hidden property="id" />
		<div id="task">
		<table class="formcontent">
			<tr>
				<td class="fieldname" width='180'><fmt:message key="task.deadline" /></td>
				<td>
					<html:text property="deadlineStr" size='15' maxlength='15' />
				</td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="task.title" /></td>
				<td><html:text property="title" size='1024' /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="task.description" /></td>
				<td><html:text property="description" size='64000' /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="task.nodetype" /></td>
				<td>
				<html:select property="nodetype">
                     <mm:listnodes type="editwizards">
                        <mm:field name="nodepath" jspvar="nodepath" id="nodepath" vartype="String">
                           <% if (ContentElementUtil.isContentType(nodepath)) { %>
		                      <html:option value="${nodepath}"><mm:nodeinfo nodetype="${nodepath}" type="guitype"/></html:option>
                           <% } %>
                        </mm:field>
                     </mm:listnodes>
                  </html:select>
				</td>
			</tr>
         <tr>
            <td class="fieldname">contentelement</td>
            <td>
               <html:select property="user" size="1" styleId="users">
                  <html:optionsCollection name="usersList" value="value" label="label"/> 
               </html:select> 
            </td>
         </tr>
         <tr>
				<td class="fieldname"><fmt:message key="task.user" /></td>
				<td>
					<html:select property="user" size="1" styleId="users">
						<html:optionsCollection name="usersList" value="value" label="label"/> 
					</html:select> 
				</td>
			</tr>
		</table>
		</div>
		<br />
		<table>
			<tr>
				<td><html:submit style="width:90"><fmt:message key="task.submit"/></html:submit></td>
				<td><html:cancel style="width:90"><fmt:message key="task.cancel"/></html:cancel></td>
			</tr>
		</table>
	</html:form>
</mm:cloud>
</body>
</html:html>
</mm:content>