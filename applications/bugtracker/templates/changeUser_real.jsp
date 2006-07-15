<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="portal">none</mm:import>
<mm:import externid="page">none</mm:import>
<mm:import externid="error">none</mm:import>
<form action="showMessage.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&action=checkuser" method="POST">
<center>
<table width="90%" cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list">
<mm:compare referid="error" value="login">
<tr>
	<td COLSPAN=2>
	feedback <font color="#cc0000">** wrong account or password **</font>
	</td>
</tr>
</mm:compare>

<tr>
		<th>
		Login
		</th>
		<td>
			<INPUT NAME="account" VALUE="" SIZE="10"><BR>
		</td>
</tr>
<tr>
		<th>
		Password
		</th>
		<td>
			<INPUT NAME="password" VALUE="" SIZE="10" TYPE="password"></FONT>
		</td>
</tr>
<tr>
		<td COLSPAN="2">
			<CENTER>
			<INPUT TYPE="SUBMIT" VALUE="Login">
			<BR>
			<BR>
forgot my password mail me the info <A HREF="sendAccountInfo.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />">resend info</A><br>
I don't have a password yet <A HREF="/development/bugtracker/newUser.jsp">get account</A>
		</td>
</tr>
</table>
</form>
</body>
</mm:cloud>
