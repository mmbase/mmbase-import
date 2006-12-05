<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
<tr>
	<th>
	Name
	</th>
	<th>
	Maintainer
	</th>
	<th>
	Version
	</th>
	<th>
	State
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getBundles">
<TR>
		<td align="left">
			<mm:import id="pid"><mm:field name="id" /></mm:import>
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="bundle" /><mm:param name="id" value="$pid" /></mm:url>"><mm:field name="name" /></a>
		</td>
		<td align="left">
			<mm:field name="maintainer" />	
		</td>
		<td align="left">
			<mm:field name="version" />	
		</td>
		<td align="left">
			<mm:field name="state" />	
		</td>
		<td width="15">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="bundle" /><mm:param name="id" value="$pid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
		<mm:remove referid="pid" />
</tr>
 </mm:nodelistfunction>

</table>
