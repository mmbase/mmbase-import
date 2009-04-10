<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="user.edit.title">
	<style>
		input.select { font-height: 4px;}
	</style>
</cmscedit:head>
<body style="overflow: auto">
<cmscedit:contentblock title="user.edit.title" titleClass="content_block_pink">
<mm:cloud loginpage="../login.jsp" rank='administrator'>
	<mm:cloudinfo type="user" id="cloudusername" write="false" />
	<html:form action="/editors/usermanagement/UserAction">
		<html:hidden property="id" />
		<div id="user">
		<table class="formcontent">
         <tr>
            <td><fmt:message key="user.notice" /></td>
         </tr>
			<tr>
				<td class="fieldname" width='180'><fmt:message key="user.account" />*</td>
				<td>
					<logic:equal name="UserForm" property="id" value="-1">
						<html:text property="username" size='15' maxlength='15' />
						<span class="notvalid"><html:errors bundle="SECURITY" property="username" /></span>
					</logic:equal> 
					<logic:notEqual name="UserForm" property="id" value="-1">
						<bean:write name="UserForm" property="username" />
					</logic:notEqual>
				</td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.firstname" /></td>
				<td><html:text property="firstname" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.prefix" /></td>
				<td><html:text property="prefix" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.surname" /></td>
				<td><html:text property="surname" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.email" />*</td>
				<td><html:text property="email" size='30' maxlength='255' />
					<span class="notvalid"><html:errors bundle="SECURITY" property="email" /></span>
				</td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.company" /></td>
				<td><html:text property="company" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.department" /></td>
				<td><html:text property="department" size='30' maxlength='40' />&nbsp;&nbsp;<fmt:message key="user.textlength" /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.note" /></td>
				<td><html:textarea property="note" cols='60' rows='5' /></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.password" />*</td>
				<td>
					<html:password property="password1" size='15' maxlength='15' />
					<span class="notvalid"><html:errors bundle="SECURITY" property="password1" /></span>
				</td>
			</tr>
			<tr>
				<td class="fieldname" nowrap><fmt:message key="user.confirmpassword" />*</td>
				<td>
					<html:password property="password2" size='15' maxlength='15' />
					<span class="notvalid"><html:errors bundle="SECURITY" property="password2" /></span>
				</td>
			</tr>
	      <tr>
	      
	         <td class="fieldname" nowrap width="150"><fmt:message key="changelanguage.language" /></td>
		      <td class="fieldname">
		      	<html:select property="language">
		      		<html:option value=""><fmt:message key="changelanguage.default" /></html:option>
		      		<html:option value="en"><fmt:message key="changelanguage.english" /></html:option>
		      		<html:option value="nl"><fmt:message key="changelanguage.dutch" /></html:option>
		      		<html:option value="zh"><fmt:message key="changelanguage.chinese" /></html:option>
		      	</html:select>
		      </td>
		   </tr>
		   	<tr><td class="fieldname" nowrap><fmt:message key="user.emailsignal" /></td>
				<td class="field"><html:checkbox property="emailSignal" style="width: auto;"/></td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.rank" /></td>
				<td>
					<logic:equal name="UserForm" property="username" value="admin">
						<fmt:message key="user.rank.admin" />
					</logic:equal> 
					<logic:notEqual property="username" name="UserForm" value="admin">
						<logic:equal name="UserForm" property="username" value="${cloudusername}">
							<fmt:message key="user.rank.admin" />
						</logic:equal> 
						<logic:notEqual property="username" name="UserForm" value="${cloudusername}">
							<html:select property="rank" size="1">
								<html:optionsCollection label="description" value="value"
									property="ranks" name="UserForm" />
							</html:select>
						</logic:notEqual>
					</logic:notEqual>
				</td>
			</tr>
			<tr>
				<td class="fieldname"><fmt:message key="user.status" /></td>
				<td>
					<logic:equal name="UserForm" property="username" value="admin">
						<fmt:message key="user.status.active" />
					</logic:equal> 
					<logic:notEqual property="username" name="UserForm" value="admin">
						<logic:equal name="UserForm" property="username" value="${cloudusername}">
							<fmt:message key="user.status.active" />
						</logic:equal> 
						<logic:notEqual property="username" name="UserForm" value="${cloudusername}">
							<html:select property="status" size="1">
								<html:optionsCollection label="description" value="value"
									property="statuses" name="UserForm" />
							</html:select>
						</logic:notEqual>
					</logic:notEqual>
				</td>
			</tr>
		</table>
		</div>

		<br />
		<div style="padding: 5px;">
			<html:submit style="width:90"><fmt:message key="user.submit"/></html:submit>
			<html:cancel style="width:90"><fmt:message key="user.cancel"/></html:cancel>
		</div>
	</html:form>
</mm:cloud>
</cmscedit:contentblock>	
</body>
</html:html>
</mm:content>