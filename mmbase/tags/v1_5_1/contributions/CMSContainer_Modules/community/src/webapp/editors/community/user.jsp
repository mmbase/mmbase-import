<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<%@ taglib uri="http://finalist.com/cmsc/community" prefix="community" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="view.title"/>
<body style="overflow: auto">
<cmscedit:contentblock title="view.title" titleClass="content_block_pink">
<mm:cloud loginpage="../login.jsp" rank='administrator'>
	<mm:cloudinfo type="user" id="cloudusername" write="false" />
   <html:form action="/editors/community/userAddAction?group=${groupName},${option}">
		<div id="user">
		<table class="formcontent">
     
			<tr>
            <td class="fieldname" width='180'><fmt:message key="view.user" /></td>
				<td>

               <logic:equal name="communityUserForm" property="action" value="add">
                  <html:text property="account" size="30" />
                  <span class="notvalid"><html:errors bundle="COMMUNITY" property="account" /></span>
               </logic:equal> 
               <logic:notEqual name="communityUserForm" property="action" value="add">
                  <bean:write name="communityUserForm" property="account" />
               </logic:notEqual>

				</td>
			</tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.firstname" /></td>
            <td><html:text property="firstName" size='30' />
            <span class="notvalid"><html:errors bundle="COMMUNITY" property="firstName" /></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.prefix" /></td>
            <td><html:text property="prefix" size="15" />
            <span class="notvalid"><html:errors bundle="COMMUNITY" property="prefix" /></span>
            </td>
         </tr>         
         <tr>
            <td class="fieldname"><fmt:message key="view.surname" /></td>
            <td><html:text property="lastName" size='30' />
            <span class="notvalid"><html:errors bundle="COMMUNITY" property="lastName" /></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.email" /></td>
            <td><html:text property="email" size='30' />
            <span class="notvalid"><html:errors bundle="COMMUNITY" property="email" /></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname"><fmt:message key="view.password" /></td>
            <td>
               <html:password property="passwordText" size="15" maxlength="15" />
               <span class="notvalid"><html:errors bundle="COMMUNITY" property="passwordText" /></span>
            </td>
         </tr>
         <tr>
            <td class="fieldname" nowrap><fmt:message key="view.confirmpassword" /></td>
            <td>
               <html:password property="passwordConfirmation" size="15" maxlength="15" />
               <span class="notvalid"><html:errors bundle="COMMUNITY" property="passwordConfirmation" /></span>
            </td>
         </tr>
           
		</table>
		</div>

		<br />
		<div style="padding: 5px;">
			<html:submit style="width:90"><fmt:message key="view.submit"/></html:submit>
			<html:cancel style="width:90"><fmt:message key="view.cancel"/></html:cancel>
		</div>
	</html:form>
</mm:cloud>
</cmscedit:contentblock>	
</body>
</html:html>
</mm:content>