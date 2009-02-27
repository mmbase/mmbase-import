<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="../globals.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:content type="text/html" encoding="UTF-8" expires="0">
<fmt:setBundle basename="newsletter" scope="request" />
<html:html xhtml="true">
<cmscedit:head title="testinput.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
   <script src="check.js" type="text/javascript"></script>
   <script language="javascript">
      function sendEmail() {
         var email = document.getElementById("email");
         if(!isEmail(email.value)) {
            alert('<fmt:message key="testinput.email.incorrect"/>');
            email.focus();
            return;
         }
         document.forms[0].submit()
      }
      function cancel() {
         var action = document.getElementById("action");
         action.value = "cancel";
         document.forms[0].submit()
      }
   </script>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="testinput.title" titleClass="side_block_green">
	<p>
		<fmt:message key="testinput.subtitle" />
	</p>
   <c:url var="actionUrl" value="/editors/newsletter/NewsletterPublicationTest.do"/>
	<form action="${actionUrl}" method="post">
      <input type="hidden" name="number" id="number" value="${number}"/>
      <input type="hidden" name="action" id="action" value="send"/>
      <mm:import externid="forward" />
      <mm:import externid="newsletterId"/>
      <input type="hidden" name="forward" value="${forward}"/>
      <input type="hidden" name="newsletterId" value="${newsletterId}"/>
      <table>
         <tr>
            <td><fmt:message key="testinput.email"/></td>
            <td><input type="text" name="email" id="email" style="width:170px"/></td>
         </tr>
         <tr>
            <td><fmt:message key="testinput.mimetype"/></td>

            <td>
               <select name="mimetype" id="mimetype">
                <option value="text/html">text/html</option>
                <option value="text/plain">text/plain</option>
             </select>
            </td>
        </tr>
      </table>
      <br/>
         <input type="button" onclick="sendEmail()" value='<fmt:message key="testinput.send"/>'/>
         <input type="button" onclick="cancel()" value='<fmt:message key="testinput.cancel"/>'/>
	</form>

</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>