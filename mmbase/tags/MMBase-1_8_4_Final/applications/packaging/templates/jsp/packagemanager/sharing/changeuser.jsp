<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="45%">
<tr>
	<th colspan="2">
	Change User 
	</th>
</tr>
		<mm:functioncontainer>
		  <mm:param name="account" value="$id" />
		  <mm:nodelistfunction set="mmpm" name="getUserInfo">
<form action="<mm:url page="index.jsp">
				<mm:param name="main" value="$main" />
				<mm:param name="sub" value="allusers" />
				</mm:url>" method="post">
		  <tr>
		  <td>
			Account
		  </td>
		  <td>
			<mm:field name="account" />
			<input type="hidden" name="newaccount" value="<mm:write referid="id" />">
			<input type="hidden" name="action" value="changeuser">
		  </td>
		  </tr>
		  <tr>
		  <td>
			Password
		  </td>
		  <td>
			<input name="newpassword" value="<mm:field name="password" />" size="12">
		  </td>
		  </tr>

		  <tr>
		  <td>
			Methode
		  </td>
		  <td>
			<select name="newmethod">
			<mm:field name="method">
			<mm:compare value="http">
			<option>http
			<option>https
			</mm:compare>
			<mm:compare value="https">
			<option>https
			<option>http
			</mm:compare>
			</mm:field>
		  </td>
		  </tr>

		  <tr>
		  <td>
			IP Filter
		  </td>
		  <td>
			<input name="newhost" size="12" value="<mm:field name="host" />" >
		  </td>
		  </tr>
		  <tr>
		  <td colspan="2" align="center">
			<input type="submit" value="update user">
		  </td>
		  </tr>
</form>
</mm:nodelistfunction>
</mm:functioncontainer>
</table>
