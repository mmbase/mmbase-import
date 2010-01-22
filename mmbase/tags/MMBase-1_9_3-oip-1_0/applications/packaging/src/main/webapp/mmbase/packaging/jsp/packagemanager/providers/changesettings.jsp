<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="providers/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="65%">
<tr>
<td>
<form action="" method="post">
<table cellpadding="0" cellspacing="0" class="list"  width="100%">
<tr>
	<th COLSPAN="2">
	Provider information <mm:write referid="action" />
	</th>
</tr>
		<mm:functioncontainer>
		  <mm:param name="name" value="$id" />
		  <mm:nodelistfunction set="mmpm" name="getProviderInfo">

		  <tr>
		  <th width="100">
			Name
		  </th>
		  <td width="100">
			<mm:field name="name" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Method
		  </th>
		  <td>
			<mm:field name="method" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Maintainer
		  </th>
		  <td>
			<input size="25" name="maintainer" value="<mm:field name="maintainer" />">
		  </td>
		  </tr>

		  <tr>
		  <th>
			Account
		  </th>
		  <td>
			<input size="10" name="account" value="<mm:field name="account" />">
		  </td>
		  </tr>

		  <tr>
		  <th>
			Password
		  </th>
		  <td>
			<input size="10" type="password" name="password" value="<mm:field name="password" />">
		  </td>
		  </tr>

		  <tr>
		  <th>
			Share Path
		  </th>
		  <td>
			<input size="60" name="path" value="<mm:field name="path" />">
		  </td>
		  </tr>


		  <tr>
		  <th>
		  </th>
		  <td align="center">
			<input type="hidden" name="action" value="changesettings">
			<input type="submit" value="save these settings">
		  </td>
		  </tr>

		  </mm:nodelistfunction>
		</mm:functioncontainer>
</table>
</form>
</td>
</tr>
</table>

