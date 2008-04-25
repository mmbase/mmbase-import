<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="administrator">
<%@ include file="../thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check --> 

	<table align="center" cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="55%">
		<tr><th colspan="2">MMBob Auto Generator</th></tr>
		<tr><td colspan="2" height="40" align="center"><b>THIS PAGE GENERATES FAKE USERS BE CAREFUL</b></td></tr>
		<tr>	
			<form action="users.jsp" method="post">
				<td>Number of areas to be created</td>
				<td><select name="generatecount">
					<option>1
					<option>10
					<option>100
					<option>1000
				</select>
		</tr>
		<tr>	
				<td>Delay between each generate</td>
				<td><select name="generatedelay">
					<option value="10">10 ms
					<option value="100" >100 ms
					<option value="1000">1 sec
					<option value="10000">10 sec
				</select>
		</tr>
		<tr>
				<td>Select in which forum</td>
				<td>
				<select name="inforum">
  					<mm:nodelistfunction set="mmbob" name="getForums" max="20">
                        		<option value="<mm:field name="id" />"><mm:field name="name" />
  					</mm:nodelistfunction>
					<option>all
				</select>
				</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<input type="hidden" name="action" value="generateareas">
			<input type="submit" value="generate" />
			</td>
		</tr>
		</form>
	</table>
</mm:cloud>
</center>
</html>
