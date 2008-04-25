<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html; charset=utf-8"
%><mm:cloud
><%@include file="/includes/getids.jsp" 
%><%@include file="/includes/header.jsp"
%><%

String personId = request.getParameter("person");
if(personId==null) personId = "";
String firstnameId = request.getParameter("firstname");
if(firstnameId==null) firstnameId = "";
String lastnameId = request.getParameter("lastname");
if(lastnameId==null) lastnameId = "";
String nicknameId = request.getParameter("nickname");
if(nicknameId==null) nicknameId = "";
String organisationId = request.getParameter("organisation");
if(organisationId==null) organisationId = "default";
String groupsId = request.getParameter("groups");
if(groupsId==null) groupsId = "default";
String submitId = request.getParameter("submit");
if(submitId==null) submitId = "";
String loginId = request.getParameter("login");
if(loginId==null) loginId = "";
String pwdId = request.getParameter("pwd");
if(pwdId==null) pwdId = "";

%><td class="white" colspan="2" valign="top">
	<table cellpadding="0" cellspacing="0" class="layout" style="width:100%;">
		<tr><%

if(loginId.equals("")){ 	// user did not try to log in  
	if(!personId.equals("")) {
		%><td width="454px"><%@include file="includes/person.jsp" %></td><%
	} else {
		%><mm:list nodes="$page" path="pages,posrel,articles" max="1"
			><mm:node element="articles"
				><td width="454px"><%@include file="/includes/article.jsp" %></td>
			</mm:node
		></mm:list><%
	}

} else if(pwdId.equals("")) {	// create an account and send the user his/her password

	%><mm:node number="<%= personId %>"
		><mm:import id="email"><mm:field name="email" /></mm:import
		><mm:import id="firstname"><mm:field name="firstname" /></mm:import
		><mm:import id="lastname"><mm:field name="lastname" /></mm:import
		><mm:include page="includes/account.jsp" referids="portal,page,email,firstname,lastname" 
	/></mm:node><%

} else {			// lets see whether the user exists and provided the right password

	%><mm:node number="<%= personId %>"
		><mm:import id="email"><mm:field name="email" /></mm:import
		><mm:import id="password"><%= pwdId %></mm:import
		><mm:listnodes type="users" constraints="(email='$email' AND password='$password')" max="1">
			<mm:include page="includes/edit.jsp" referids="portal,page"><mm:param name="objectnumber"><%= personId %></mm:param></mm:include>
			<mm:import id="userfound"
		/></mm:listnodes
	><mm:notpresent referid="userfound">
		<td width="454px"><h4>The password you supplied is not correct.</h4>
		<h4>Please try again.</h4>
		<form method="POST" name="peoplefinder" action="<mm:url page="/index.jsp" referids="portal,page" />">
			<input type="text" name="pwd" size="13" value="your password" onClick="this.value='';">
			<input type="hidden" name="person" value="<mm:field name="number" />">
			<input type="submit" name="login" value="Login" style="text-align:center;font-weight:bold;">
		</form>
		<h4>or</h4>
		<form method="POST" name="peoplefinder" action="<mm:url page="/index.jsp" referids="portal,page" />">
			<input type="hidden" name="person" value="<mm:field name="number" />">
			<input type="submit" name="login" value="Resend account information" style="text-align:center;font-weight:bold;">
		</form>		
		</div>
		</td>
	</mm:notpresent
	></mm:node><% 
} 
	%><td><%@include file="includes/form.jsp" %></td>
	</tr>
</table>
</td>
<%@include file="/includes/footer.jsp" %>
</mm:cloud>
