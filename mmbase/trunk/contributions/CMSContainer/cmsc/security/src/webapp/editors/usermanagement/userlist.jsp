<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
<link href="../style.css" type="text/css" rel="stylesheet" />
<title><fmt:message key="userlist.title" /></title>
</head>
<body>
<mm:cloud loginpage="../login.jsp" rank="administrator" jspvar="cloud">

<table cellspacing="10">
	<tr><td>
		<h1><fmt:message key="userlist.groups" /></h1>
		<br />
		<a href="GroupInitAction.do"><img src="../editwizards/media/new.gif"
			border='0' align='middle' title="<fmt:message key="userlist.newgroup" />" /></a>
		<br />
	</td><td>
		<h1><fmt:message key="userlist.users" /></h1>
		<br />
		<a href="UserInitAction.do"><img src="../editwizards/media/new.gif"
			border='0' align='middle' title="<fmt:message key="userlist.newuser" />" /></a>
		<br />
	</td></tr>
	<tr><td style="vertical-align: top">
	
		<table>
			<tr>
				<th><fmt:message key="group.name" /></th>
				<th>&nbsp;</th>
			</tr>
			<mm:listnodes type='mmbasegroups' orderby='name'>
				<tr>
					<td><a href="GroupInitAction.do?id=<mm:field name='number'/>"><mm:field name="name" /></a></td>
					<mm:haspage page="/editors/usermanagement/repository/">
						<td><a href="ContentRolesInitAction.do?nodeNumber=<mm:field name='number'/>">
							<img src="../img/roles.gif" border='0' title="<fmt:message key="userlist.contentroles" />" />
						</a></td>
					</mm:haspage>
					<mm:haspage page="/editors/usermanagement/site//">
						<td><a href="SiteRolesInitAction.do?nodeNumber=<mm:field name='number'/>">
							<img src="../img/roles.gif" border='0' title="<fmt:message key="userlist.siteroles" />" />
						</a></td>
					</mm:haspage>
	
					<td><mm:maydelete>
						<a href="DeleteGroupAction.do?id=<mm:field name='number'/>">
							<img src="../img/remove.gif" border='0' title="<fmt:message key="userlist.removegroup" />"
								onclick="return confirm('<fmt:message key="userlist.removegroupquestion" />')" />
						</a>
					</mm:maydelete></td>
				</tr>
			</mm:listnodes>
		</table>
	</td><td style="vertical-align: top">
		<table>
			<tr>
				<th><fmt:message key="user.account" /></th>
				<th><fmt:message key="user.name" /></th>
				<th>&nbsp;</th>
			</tr>
			<mm:listnodes type='user' orderby='username'>
				<mm:field name="username" id="username" write="false" />
				<mm:compare referid="username" value="anonymous" inverse="true">
				<tr>
					<td><a href="UserInitAction.do?id=<mm:field name='number'/>"><mm:field name="username" /></a></td>
					<td><mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /></td>
					<td><mm:maydelete>
						<a href="DeleteUserAction.do?id=<mm:field name='number'/>">
							<img src="../img/remove.gif" border='0' title="<fmt:message key="userlist.removeuser" />"
								onclick="return confirm('<fmt:message key="userlist.removeuserquestion" />')" />
						</a>
					</mm:maydelete></td>
				</tr>
				</mm:compare>
			</mm:listnodes>
		</table>
	</td></tr>
</table>

</mm:cloud>
</body>
</html:html>
</mm:content>