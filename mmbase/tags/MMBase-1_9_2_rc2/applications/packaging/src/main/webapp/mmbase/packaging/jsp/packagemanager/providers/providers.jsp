<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="providers/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
<tr>
	<th>
	Name
	</th>
	<th>
	method
	</th>
	<th>
	maintainer
	</th>
	<th>
	state	
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getProviders">
<tr>
		<td align="left">
			<mm:field name="name" />	
		</td>
		<td align="left">
			<mm:field name="method" />	
		</td>
		<td align="left">
			<mm:field name="maintainer" />	
		</td>
		<td align="left">
			<mm:field name="state" />	
		</td>
		<mm:remove referid="name" />
		<mm:import id="name"><mm:field name="name" /></mm:import>
		<td width="20">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="provider" /><mm:param name="id" value="$name" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
</tr>
 </mm:nodelistfunction>
<tr>
 <td colspan="5" align="right">
 Add Provider
 <A HREF="<mm:url page="index.jsp">
	<mm:param name="main" value="$main" />
	<mm:param name="sub" value="addprovider" />
	</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>
</tr>

</table>
