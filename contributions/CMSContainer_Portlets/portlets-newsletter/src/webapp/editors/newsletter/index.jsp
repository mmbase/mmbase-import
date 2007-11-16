<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="portlets-newsletter" scope="request" />

<h3><fmt:message key="newsletter.overview.title" /></h3>

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">  


<table width="25%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>&nbsp;</td>
		<td><fmt:message key="newsletter.name" />	</td>
		<td><fmt:message key="newsletter.subscriberamount" /></td>
	</tr>
	<tr>
		<td>
		</td>
		<td>
		</td>
		<td></td>
</table>
<a href="action.jsp">Nieuwsbrief test</a>

</mm:cloud>



