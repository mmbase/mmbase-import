<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="settings/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="95%">
<tr>
	<th colspan="2">
	Loaded Creators
	</th>
</tr>
<tr>
	<th>Method</th>
	<th>Handler</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getCreators">
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
