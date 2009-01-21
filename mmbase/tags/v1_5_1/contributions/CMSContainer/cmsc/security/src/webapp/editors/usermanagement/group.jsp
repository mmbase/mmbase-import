<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="group.title">
	<script src="../../js/selectbox.js" type="text/javascript"></script>
	<style>
		input.select { font-height: 4px;}
	</style>
</cmscedit:head>
<body style="overflow: auto">
<mm:cloud jspvar="cloud" loginpage="../login.jsp" rank='administrator'>
<cmscedit:contentblock title="group.title" titleClass="content_block_pink">
	<html:form action="/editors/usermanagement/GroupAction" 
		 onsubmit="return selectboxesOnSubmit('users', 'members');">
		<html:hidden property="id" />
		<div id="group">
		<table>
			<tr>
				<td class="fieldname" width='180'><fmt:message key="group.name" /></td>
				<td>
					<logic:equal name="GroupForm" property="id" value="-1">
						<html:text property="name" size='15' maxlength='15' />
						<span class="notvalid"><html:errors bundle="SECURITY" property="groupname" /></span>
					</logic:equal> 
					<logic:notEqual name="GroupForm" property="id" value="-1">
						<bean:write name="GroupForm" property="name" />
					</logic:notEqual>
				</td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="group.description" /></td>
				<td><html:text property="description" size='30' /></td>
			</tr>
		</table>
		</div>

		<table>
			<tr>
				<td width="180">
					<fmt:message key="group.nonmembers" /><br />
					<html:select property="users" size="25" styleId="users" multiple="true" style="width: 180px">
						<html:optionsCollection name="usersList" value="value" label="label"/> 
					</html:select> 
				</td>
				<td style="vertical-align:middle" width="30">
					<input type="button" class="flexbutton" value="&gt;&gt;" onClick="one2two('users', 'members', true)" />
					<br/>
					<input type="button" class="flexbutton" value="&lt;&lt;" onClick="two2one('users', 'members', true)"/>
				</td>
				<td>
					<fmt:message key="group.members" /><br />
					<html:select property="members" size="25" styleId="members" multiple="true" style="width: 180px">
						<html:optionsCollection name="membersList" value="value" label="label"/> 
					</html:select> 
				</td>
			</tr>
		</table>

		<br />
		<div style="padding: 5px;">
			<html:submit style="width:90"><fmt:message key="group.submit"/></html:submit>
         <html:cancel style="width:90"><fmt:message key="group.cancel"/></html:cancel>
		</div>
	</html:form>
	</cmscedit:contentblock>	
</mm:cloud>
</body>
</html:html>
</mm:content>
