<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@include file="globals.jsp"  %>
<c:choose>
<c:when test="${done}">
<script>
	top.document.location = "../index.jsp";
</script>
</c:when>
<c:otherwise>

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="changelanguage.title" />
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <div class="tabs">
         <!-- actieve TAB -->
         <div class="tab_active">
            <div class="body">
               <div>
                  <a name="activetab"><fmt:message key="changelanguage.title" /></a>
               </div>
            </div>
         </div>
      </div>

    <div class="editor">
      <div class="body">



<mm:cloudinfo type="user" id="username" write="false"/>
<mm:listnodes type="user" constraints="username='${username}'">
	<mm:field name="language" jspvar="language" write="false"/>
	<html:form action="/editors/usermanagement/ChangeLanguageAction">
	   <table class="formcontent">
	      <tr>
	         <td class="fieldname" nowrap width="150"><fmt:message key="changelanguage.language" /></td>
		      <td class="fieldname">
		      	<html:select property="language">
		      		<option value="en" <c:if test="${language == 'en'}">selected</c:if>><fmt:message key="changelanguage.english" /></option>
		      		<option value="nl" <c:if test="${language == 'nl'}">selected</c:if>><fmt:message key="changelanguage.dutch" /></option>
		      		<option value="zh" <c:if test="${language == 'zh'}">selected</c:if>><fmt:message key="changelanguage.chinese" /></option>
		      	</html:select>
		      </td>
		   </tr>
		   <tr>
		      <td>&nbsp;</td>
		      <td>
		      <html:submit style="width:90"><fmt:message key="changelanguage.submit" /></html:submit>
	 		  <html:cancel style="width:90"><fmt:message key="user.cancel"/></html:cancel>
		      </td>
		   </tr>
		</table>
	</html:form>
</mm:listnodes>
</mm:cloud>
      </div>
   </div>
</body>
</html:html>
</mm:content>
</c:otherwise>
</c:choose>