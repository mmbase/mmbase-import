<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="65%" border="0" align="left">
<tr>
<td align="middle">
<table cellpadding="0" cellspacing="0" class="list">
<tr>
	<th COLSPAN="2" width="100%">
	Change user 
	</th>
</tr>
<form action="index.jsp" method="get">
		  <tr>
		  <td width="50%">
			<select name="id">
				<mm:nodelistfunction set="mmpm" name="getAllUsers">
					<option><mm:field name="name" />
				</mm:nodelistfunction>
			</select>
		  </td>
		  <td width="50%">
			<input type="hidden" name="main" value="sharing" />
			<input type="hidden" name="sub" value="changeuser" />
			<center><input type="submit" value="change"></center>
		  </td>
		  </tr>
</form>
</TABLE>
</td>
</tr>


<tr>
<td align="middle">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;">
<tr>
	<th COLSPAN="2">
	Delete user
	</th>
</tr>
	<form action="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="deluser" />
					</mm:url>" method="post">

		  <tr>
		  <td>
			<select name="id">
				<mm:nodelistfunction set="mmpm" name="getAllUsers">
					<option><mm:field name="name" />
				</mm:nodelistfunction>
			</select>
		  </td>
		  <td>
			<center><input type="submit" value="Delete"></center>
		  </td>
		  </tr>
</form>
</table>
</td>
</tr>

<tr>
<td align="middle">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;">
<tr>
	<th COLSPAN="2">
	New User
	</th>
</tr>
<form action="" method="post">
		  <tr>
		  <th> 
			Account
		  </th>
		  <td>
			<input name="newuser" size="12">
			<input type="hidden" name="action" value="newuser">
		  </td>
		  </tr>
		  <tr>
		  <th>
			Password
		  </th>
		  <td>
			<input name="newpassword" size="12">
		  </td>
		  </tr>

		  <tr>
		  <th>
			Methode
		  </th>
		  <td>
			<select name="newmethod">
			<option>http
			<option>https
		  </td>
		  </tr>

		  <tr>
		  <th>
			IP Filter
		  </th>
		  <td>
			<input name="newip" size="12" value="none" >
		  </td>
		  </tr>
		  <tr>
		  <td colspan="2" align="middle">
			<input type="submit" value="create user">
		  </td>
		  </tr>
</form>
</table>
</td>
</tr>
</table>

