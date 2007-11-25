<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="application" />

<h3><fmt:message key="stats.title" /></h3>
<p><fmt:message key="stats.intro" /></p>

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">  


<table width="25%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fmt:message key="stats.total.newsletters" /></td>
		<td>
			<mm:listnodes type="newsletter">
				<mm:first><mm:size /></mm:first>
			</mm:listnodes>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="stats.total.themes" /></td>
		<td>
			<mm:listnodes type="newslettertheme">
				<mm:first><mm:size /></mm:first>
			</mm:listnodes>
		</td>
	</tr>
	<tr>
		<td><fmt:message key="stats.total.publications" /></td>
		<td>
			<mm:listnodes type="newsletterpublication">
				<mm:first><mm:size /></mm:first>
			</mm:listnodes>
		</td>
	</tr>

</table>

</mm:cloud>



