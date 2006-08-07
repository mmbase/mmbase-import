<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link rel="stylesheet" type="text/css" href="../css/main.css" />
<title><fmt:message key="changepassword.title" /></title>
</head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

<%
   String username = cloud.getUser().getIdentifier();
   String succeeded = request.getParameter("succeeded");
%>

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



<%
   if ((succeeded != null) && (succeeded.equals("true"))) {
%>
      <font color="#FF0000"><b><fmt:message key="changepassword.succeeded" /></b></font>
<%
   }
%>
<html:form action="/editors/usermanagement/ChangePasswordAction">
   <table class="formcontent">
      <tr>
         <td class="fieldname" nowrap width="150"><fmt:message key="changepassword.current" /></td>
	      <td class="fieldname">
	         <html:password property="password" size='15' maxlength='15'/></font>
	         <span class="notvalid"><html:errors bundle="SECURITY" property="password"/></span>
	      </td>
	   </tr>
	   <tr>
         <td class="fieldname" nowrap><fmt:message key="changepassword.new" /></td>
	      <td class="fieldname">
	         <html:password property="newpassword" size='15' maxlength='15'/></font>
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
</html:html>
</mm:content>