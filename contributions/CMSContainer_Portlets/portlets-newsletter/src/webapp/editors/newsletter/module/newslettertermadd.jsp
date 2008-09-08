<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newsletter.term.title">
	<c:url var="actionUrl" value="/editors/community/PreferenceAction.do"/>
	<script type="text/javascript">
		function add() {
	      document.forms[0].submit();
	   }
		function cancel() {
         document.forms[0].name.value = "";
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
                  <a href="#"><fmt:message key="newsletter.term.title" /></a>
               </div>
            </div>
         </div>
      </div>

     <div class="editor">
         <div class="body">
            <html:form action="/editors/newsletter/module/NewsletterTermAction" method="post">
               <html:hidden property="method" value="add"/>
               <html:hidden property="offset" value="0"/>
               <table border="0" >
                  <tr>
                     <td width="110px"><fmt:message key="newsletter.term.name" /></td>
                     <td><html:text style="width: 150px" property="name"/>
                     <logic:messagesPresent message="true">
                        <html:messages id="msg" message="true" bundle="newsletter">
                           <bean:write name="msg"/> <br/>
                        </html:messages>
                     </logic:messagesPresent>
                     </td>
                  </tr>
                   <tr>
                     <td> </td>
                     <td>
                     <input type="submit" name="submitButton" onclick="add();" 
                     value="<fmt:message key="newsletter.term.action.save" />"/><c:forEach var="space" begin="1" end="10" step="1">&nbsp; </c:forEach>
                     <input type="submit" name="submitButton" onclick="cancel();" 
                     value="<fmt:message key="newsletter.term.action.cancel" />"/>
                     </td>
                </tr>
               </table>
            </html:form>
         </div>
      </div>

</mm:cloud>
   </body>
</html:html>
</mm:content>