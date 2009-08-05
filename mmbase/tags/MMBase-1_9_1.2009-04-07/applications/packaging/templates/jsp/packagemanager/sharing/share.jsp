<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr>
<td width="50%" align="middle">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
<tr>
	<th colspan="2">
	Package Share Information
	</th>
</tr>
		  <mm:nodefunction set="mmpm" name="getShareInfo" referids="id,version">
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
			Type
		  </th>
		  <td>
			<mm:field name="type" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Maintainer
		  </th>
		  <td>
			<mm:field name="maintainer" />
		  </td>
		  </tr>

		  <tr>
		  <th>
			Active
		  </th>
		  <td>
			<mm:field name="active" />
		  </td>
		  </tr>

		  </mm:nodefunction>
</table>
</td>

<td align="middle">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
<tr>
	<th colspan="2" width="200">
	Share Actions
	</th>
</tr>
		  <mm:nodefunction set="mmpm" name="getShareInfo" referids="id,version">

		  <mm:field name="active">
		  <mm:compare value="no">
		  <tr>
		  <th>
			Make Active
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="share" />
					<mm:param name="id" value="$id" />
					<mm:param name="version" value="$version" />
					<mm:param name="action" value="makeactive" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			<mm:field name="creation-date" />
		  </td>
		  </tr>
		  </mm:compare>
		  <mm:compare value="yes">
		  <tr>
		  <th>
			Make Inactive
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="share" />
					<mm:param name="id" value="$id" />
					<mm:param name="version" value="$version" />
					<mm:param name="action" value="makeinactive" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			<mm:field name="creation-date" />
		  </td>
		  </tr>
		  </mm:compare>
		  </mm:field>
		  <tr>
		  <th>
			Delete Share
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="delshare"/>
					<mm:param name="id" value="$id" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>

		  <tr>
		  <th>
			All Users
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="allusers"/>
					<mm:param name="id" value="$id" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>
		  <tr>
		  <th>
			All Groups
		  </th>
		  <td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="allgroups"/>
					<mm:param name="id" value="$id" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		  </td>
		  </tr>
		  </mm:nodefunction>
</table>
</td>
</tr>

<tr>
<td align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;">
<tr>
	<th colspan="2" width="200">
	Shared for users
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getShareUsers" referids="id,version" >

		  <tr>
		  <th width="100">
			Name
		  </th>
		  <td width="100">
			<mm:field name="name" />
		  </td>
		  </tr>

		  </mm:nodelistfunction>
		<tr>
		 <td colspan="2" align="right" valign="top">
			Change <A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="changeusers" />
					<mm:param name="id" value="$id" />
					<mm:param name="version" value="$version" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
		 </td>
		</tr>
</table>
</td>
<td align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top: 25px;">
<tr>
	<th colspan="2" width="200">
	Shared for groups
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getShareGroups" referids="id,version">
		  <tr>
		  <th width="100" align="middle">
			Name
		  </th>
		  <td width="100">
			<mm:field name="name" />
		  </td>
		  </tr>

		  </mm:nodelistfunction>
		<tr>
		 <td colspan="2" align="right">
			Change <A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="changegroups" />
					<mm:param name="id" value="$id" />
					<mm:param name="version" value="$version" />
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
		 </td>
		</tr>
</table>
</td>
</tr>
</table>

