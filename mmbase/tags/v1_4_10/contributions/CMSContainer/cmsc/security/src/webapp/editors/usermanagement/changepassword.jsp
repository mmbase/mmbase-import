<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="changepassword.title" />

<c:choose>
<c:when test="${param.succeeded}">
      <body onload="alert('<fmt:message key="changepassword.succeeded" />')">
      </body>
</c:when>
<c:otherwise>
	<body>
	<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <!-- actieve TAB -->
         <div class="tab_active">
            <div class="body">
               <div>
                  <a name="activetab"><fmt:message key="changepassword.title" /></a>
               </div>
            </div>
         </div>
      </div>

    <div class="editor">
      <div class="body">



<html:form action="/editors/usermanagement/ChangePasswordAction">
   <table class="formcontent">
      <tr>
         <td class="fieldname" nowrap width="150"><fmt:message key="changepassword.current" /></td>
	      <td class="fieldname">
	         <html:password property="password1" size='15' maxlength='15'/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="password1"/></span>
	      </td>
	   </tr>
	   <tr>
         <td class="fieldname" nowrap><fmt:message key="changepassword.new" /></td>
	      <td class="fieldname">
	         <html:password property="newpassword" size='15' maxlength='15'/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="newpassword"/></span>
	      </td>
	   </tr>
      <tr>
   	   <td class="fieldname" nowrap><fmt:message key="changepassword.confirm" /></td>
	      <td class="fieldname">
	         <html:password property="confirmnewpassword" size='15' maxlength='15'/>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="confirmnewpassword"/></span>
         </td>
	   </tr>
	   <tr>
	      <td>&nbsp;</td>
	      <td>
	      <html:submit style="width:90"><fmt:message key="changepassword.submit" /></html:submit>
 		  <html:cancel style="width:90"><fmt:message key="user.cancel"/></html:cancel>
	      </td>
	   </tr>
	</table>
</html:form>
</mm:cloud>
      </div>
   </div>
</body>
</c:otherwise>
</c:choose>
</html:html>
</mm:content>