<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
<tr>
	<th>
	Shared Bundles
	</th>
	<th>
	type
	</th>
	<th>
	maintainer
	</th>
	<th>
	active
	</th>
	<th>
	shared version(s)
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getSharedBundles">
<tr>
		<td>
			<mm:field name="name" />	
			<mm:import id="pid"><mm:field name="id" /></mm:import>
		</td>
		<td>
			<mm:field name="type" />	
		</td>
		<td>
			<mm:field name="maintainer" />	
		</td>
		<td>
			<mm:field name="active" />	
		</td>
		<td>
			<mm:field name="sharedversions" />	
		</td>
		<td width="20">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="bundleshare" /><mm:param name="id" value="$pid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
		<mm:remove referid="pid" />
</tr>
  </mm:nodelistfunction>
<tr>
 <td colspan="6" align="right">
 Add Bundle Share
 <A HREF="<mm:url page="index.jsp">
	<mm:param name="main" value="$main" />
	<mm:param name="sub" value="addbundleshare" />
	</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>
</tr>

</table>



<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th>
	Shared Packages
	</th>
	<th>
	type
	</th>
	<th>
	maintainer
	</th>
	<th>
	active
	</th>
	<th>
	shared version(s)
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="mmpm" name="getSharedPackages">
<tr>
		<td>
			<mm:field name="name" />	
			<mm:import id="pid"><mm:field name="id" /></mm:import>
		</td>
		<td>
			<mm:field name="type" />	
		</td>
		<td>
			<mm:field name="maintainer" />	
		</td>
		<td>
			<mm:field name="active" />	
		</td>
		<td>
			<mm:field name="sharedversions" />	
		</td>
		<td width="20">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="share" /><mm:param name="id" value="$pid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
		<mm:remove referid="pid" />
</tr>
  </mm:nodelistfunction>
<tr>
 <td colspan="6" align="right">
 Add Package Share
 <A HREF="<mm:url page="index.jsp">
	<mm:param name="main" value="$main" />
	<mm:param name="sub" value="addshare" />
	</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>
</tr>

</table>
