<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="community.preference.title">
	<c:url var="actionUrl" value="/editors/community/PreferenceAction.do"/>
	<script type="text/javascript">
		function cancel() {
         document.forms[0].action = "${actionUrl}?reload=true"
	      document.forms[0].method.value = "list";
	      document.forms[0].submit();
	   }
	</script>
</cmscedit:head>
   <body>
      <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
      <mm:import externid="action">search</mm:import><%-- either: search of select --%>
      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#"><fmt:message key="community.preference.title" /></a>
               </div>
            </div>
         </div>
      </div>
     <div class="editor" style="height:500px">
      <div class="body">
        <html:form action="/editors/community/PreferenceAction" method="post">
			<html:hidden property="method" value="add"/>
         <html:hidden property="offset" value="0"/>
         <table border="0">
            <tr>
               <td style="width: 80px"><fmt:message key="community.preference.user" /></td>
               <td><html:select property="userId">
                     <html:options name="users" />
                  </html:select>
              </td>
            </tr>
            <tr>
               <td><fmt:message key="community.preference.module" /></td>
               <td><html:text style="width: 250px" property="module"/></td>
            </tr>
            <tr>
               <td><fmt:message key="community.preference.key" /></td>
               <td><html:text style="width: 250px" property="key"/></td>
            </tr>
            <tr>
               <td><fmt:message key="community.preference.value" /></td>
               <td><html:text style="width: 250px" property="value"/></td>
            </tr>
             <tr>
               <td> </td>
             <td>
                <input type="submit" name="submitButton" 
                     value="<fmt:message key="view.group.submit" />"/><c:forEach var="space" begin="1" end="10" step="1">&nbsp; </c:forEach>
                     <input type="submit" name="submitButton" onclick="cancel();" 
                  value="<fmt:message key="view.group.cancel" />"/>
             </td>
          </tr>
         </table>
         </html:form>
	</div>
</mm:cloud>
   </body>
</html:html>
</mm:content>