<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="settings/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th colspan="2">
	Package Manager Settings
	</th>
</tr>
		<mm:nodelistfunction set="mmpm" name="getPackageManagerSettings">
<form action="<mm:url page="index.jsp">
				<mm:param name="main" value="$main" />
				<mm:param name="sub" value="none" />
				</mm:url>" method="post">
		  <tr>
		  <th WIDTH="150">
			Provider name
		  </th>
		  <td WIDTH="150">
			<input name="providername" value="<mm:field name="providername" />" size="25">
		  </td>
		  </tr>

		  <tr>
		  <th width="150">
			Callbackurl
		  </th>
		  <td WIDTH="150">
			<input name="callbackurl" value="<mm:field name="callbackurl" />" size="60">
			<input type="hidden" name="action" value="changesettings">
		  </td>
		  </tr>

		  <tr>
		  <td width="100" colspan="2">
			<center><input type="submit" value="update settings"></center>
		  </td>
		  </tr>
</form>
</mm:nodelistfunction>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="95%">
<tr>
	<th colspan="2">
	Loaded Package Handlers	
	</th>
</tr>
<tr>
	<th>MimeType</th>
	<th>Handler</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getPackageHandlers">
	<tr>
		<td>
		<mm:field name="name" />
		</td>
		<td>
		<mm:field name="value" />
		</td>
	</tr>
</mm:nodelistfunction>

</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="95%">
<tr>
	<th colspan="2">
	Loaded Provider Handlers	
	</th>
</tr>
<tr>
	<th>Method</th>
	<th>Handler</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getProviderHandlers">
	<tr>
		<td>
		<mm:field name="name" />
		</td>
		<td>
		<mm:field name="value" />
		</td>
	</tr>
</mm:nodelistfunction>

</table>
