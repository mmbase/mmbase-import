<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="../thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
	<table align="center" cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="55%">
		<tr><th>MMBob Auto Generator</th></tr>
		<tr><td height="40" align="center"><b>WARNING THESE ARE ADMIN FUNCTIONS AND DANGEROUS !!</b></td></tr>
		<tr>	
		  <td>
			<a href="users.jsp">Generate Users</a><br />
			<a href="areas.jsp">Generate Areas</a><br />
			<a href="threads.jsp">Generate Threads</a><br />
			<a href="replys.jsp">Generate Replys</a><br />
		  </td>
		</tr>
	</table>

</mm:cloud>
</center>
</html>
