<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="45%">
<tr>
	<th colspan="2">
	Delete confirmation
	</th>
</tr>
<tr>
  <td colspan="2" width="300">
	<br />
	&nbsp;&nbsp;Sure you want to delete user : <br /><br />
	<center>'<mm:write referid="id" />'</center>
	<br />
  </td>
</tr>
<tr>
  <td align="middle">
	<br />
	<form action="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="allusers" />
					<mm:param name="id" value="$id" /></mm:url>" method="post">
	<input type="submit" value="Oops,No "></form>
  </td>
  <td align="middle">
	<br />
	<form action="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="allusers" />
					</mm:url>" method="post">
	<input type="hidden" name="action" value="deluser">
	<input type="hidden" name="deluser" value="<mm:write referid="id" />">
	<input type="submit" value="Yes, delete"></form>
  </td>
</tr>
</table>
