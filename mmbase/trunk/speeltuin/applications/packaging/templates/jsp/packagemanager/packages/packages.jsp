<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
<tr>
	<th align="left">
	Name
	</th>
	<th align="left">
	Type
	</th>
	<th align="left">
	Maintainer
	</th>
	<th align="left">
	Version
	</th>
	<th align="left">
	State
	</th>
	<th align="left">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getPackages">
<tr>
		<td>
			<mm:import id="pid"><mm:field name="id" /></mm:import>
			<a href="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="package" /><mm:param name="id" value="$pid" /></mm:url>"><mm:field name="name" /></a>
		</td>
		<td>
			<mm:field name="type" />	
		</td>
		<td>
			<mm:field name="maintainer" />	
		</td>
		<td>
			<mm:field name="version" />	
		</td>
		<td>
			<mm:field name="state" />	
		</td>
		<td width="20">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="package" /><mm:param name="id" value="$pid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
		<mm:remove referid="pid" />
</tr>
 </mm:nodelistfunction>

</table>
