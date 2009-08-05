<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="providers/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
<tr>
<td width="50%" valign="top" align="middle">
<table cellpadding="0" cellspacing="0" class="list">
<tr>
	<th colspan="2">
	Provider information 
	</th>
</tr>
		<mm:functioncontainer>
		  <mm:param name="name" value="$id" />
		  <mm:nodelistfunction set="mmpm" name="getProviderInfo">
		  <tr>
		  <th width="100">
			Name
		  </th>
		  <td width="100" align="left">
			<mm:field name="name" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Method
		  </th>
		  <td align="left">
			<mm:field name="method" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Maintainer
		  </th>
		  <td align="left">
			<mm:field name="maintainer" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Account
		  </th>
		  <td align="left">
			<mm:field name="account" />
		  </td>
		  </tr>

		  <tr>
		  <th align="left">
			Password
		  </th>
		  <td align="left">
			<mm:field name="password" />
		  </td>
		  </tr>

		  </mm:nodelistfunction>
		</mm:functioncontainer>
</table>
</td>

<td valign="top" align="middle">
<table cellpadding="0" cellspacing="0" class="list">

<tr>
	<th colspan="2">
	Provider Actions
	</th>
</tr>
		<mm:functioncontainer>
		  <mm:param name="name" value="$id" />
		  <mm:nodelistfunction set="mmpm" name="getProviderInfo">
		  <tr>
		  <th>
			Change Settings
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="changesettings" />
					<mm:param name="id" value="$id" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>

		  <tr>
		  <th>
			Delete Provider
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="delprovider"/>
					<mm:param name="id" value="$id" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>
		  </mm:nodelistfunction>
		</mm:functioncontainer>
</table>
</td>
</tr>

<tr>
<td VALIGN="top" colspan="2" align="middle">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="65%">
<tr>
	<th colspan="2">
	Paths
	</th>
</tr>
		<mm:functioncontainer>
		  <mm:param name="name" value="$id" />
		  <mm:nodelistfunction set="mmpm" name="getProviderInfo">
		  <tr>
		  <td width="100">
			Share Path
		  </td>
		  <td align="left">
			<mm:field name="path" />
		  </td>
		  </tr>
		  </mm:nodelistfunction>
		</mm:functioncontainer>
</table>
</td>
</table>
</td>
</tr>
</table>
