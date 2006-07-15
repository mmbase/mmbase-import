<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="portal">none</mm:import>
<mm:import externid="page">none</mm:import>
<mm:import externid="error">none</mm:import>
<form action="showMessage.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&action=sendaccountinfo" method="POST">
<center>
<table width="70%" cellspacing="0" cellpadding="0" style="margin-top : 100px;" class="list">
<mm:compare referid="error" value="login">
<tr>
	<td>
	login <font color="#cc0000" />** wrong account or password **</font>
	</td>
</tr>
</mm:compare>

<tr>
		<th>
		Please give us your email so we can resend the info
		</th>
		<th>
			<INPUT NAME="email" VALUE="" SIZE="25"><BR>
		</th>
</tr>
<tr>
		<td COLSPAN="2">
			<center>
			<br>
			<INPUT TYPE="SUBMIT" VALUE="Send info">
			<br>
			<br>
		</td>
</tr>
</table>
</form>
</mm:cloud>
