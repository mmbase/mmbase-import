<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp"%>
<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="testinput.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
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
      <table>
         <tr><td>
		      email:</td>
          <td><input type="text" name="email" id="email" />
          </td></tr>
         <tr><td>
             MIMI-TYPE:</td><td>
             <select name="mimitype" id="mimitype">
                <option value="text/html">text/html</option>    
                <option value="text/plain">text/plain</option>    
             </select>
        </td></tr>
      </table>
      <br/>
	   	<html:submit property="test"><fmt:message key="testinput.send"/></html:submit>
	   	<html:submit property="cancel"><fmt:message key="testinput.cancel"/></html:submit>
	</form>

</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>