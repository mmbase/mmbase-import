<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="45%">
<tr>
	<th colspan="2">
	Delete confirmation
	</th>
</tr>
<tr>
  <td colspan="2" width="300">
	<br />
	&nbsp;&nbsp;Are you sure you want to delete share : <br /><br />
	<center>'<mm:write referid="id" />'</center>
	<br />
  </td>
</tr>
<tr>
  <td>
	<br />
	<form action="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="share" />
					<mm:param name="id" value="$id" /></mm:url>" method="post">
	<center><input type="submit" value="Oops,No "></center></form>
  </td>
  <td>
	<br />
	<form action="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="none" />
					<mm:param name="id" value="$id" />
					</mm:url>" method="post">
	<input type="hidden" name="action" value="delshare">
	<center><input type="submit" value="Yes, delete"></center></form>
  </td>
</tr>
</table>
