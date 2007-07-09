<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>
<mm:import externid="group">none</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="25%">
<tr>
	<th colspan="2">
	Groups 
	</th>
</tr>
<form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
		  <tr align="middle">
		  <td>
			<select name="group">
				  <mm:nodelistfunction set="mmpm" name="getAllGroups">
					<mm:field name="name">
					<option <mm:compare referid="group" value="$_">selected</mm:compare>><mm:field name="name" />
					</mm:field>
					<mm:first>
					<mm:compare referid="group" value="none">
						<mm:import id="group" reset="true"><mm:field name="name" /></mm:import>
					</mm:compare>
					</mm:first>
		  		  </mm:nodelistfunction>
			</select>
		  </td>
		  <td width="70" align="center">
			<input type="submit" value="select">
		  </td>
		  </tr>
		</form>
		  <form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
		  <tr>
		  <td align="middle">
			<input name="newgroup" size="15">
		  </td>
		  <td align="middle">
			<input type="hidden" name="action" value="creategroup" />
			<input type="submit" value="create group" />
		  </td>
		  </tr>
		</form>
</table>
<mm:compare referid="group" value="none" inverse="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="25%">
<tr>
	<th colspan="2">
	current users in group : <mm:write referid="group" />
	</th>
</tr>
		  <mm:nodelistfunction set="mmpm" name="getGroupUsers" referids="group">
		  <form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
		  <tr>
		  <td align="middle">
			<mm:field name="name" />
		  </td>
		  <td align="middle" width="100">
		        <input type="hidden" name="group" value="<mm:write referid="group" />">
			<input type="hidden" name="deluser" value="<mm:field name="name" />" />
			<input type="hidden" name="action" value="removegroupuser" />
			<input type="submit" value="remove">
		  </td>
		  </tr>
		</form>
		  </mm:nodelistfunction>
		  <form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
		  <tr>
		  <td align="middle" colspan="2">
			<input type="hidden" name="delgroup" value="<mm:write referid="group" />" />
			<input type="hidden" name="action" value="removegroup" />
			<input type="submit" value="delete whole group" />
		  </td>
		  </tr>
		</form>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="25%">
<tr>
	<th colspan="2">
	add  users to group : <mm:write referid="group" />
	</th>
</tr>
		  <form action="<mm:url page="index.jsp" referids="main,sub,id" />" method="post">
		  <tr>
		  <td align="middle">
			<select name="newuser">
		  	<mm:nodelistfunction set="mmpm" name="getAllUsers">
			<option><mm:field name="name" />
		  	</mm:nodelistfunction>
			</select>
		  </td>
		  <td align="middle" width="100">
			<input type="submit" value="add">
		  </td>
		  </tr>
		<input type="hidden" name="action" value="addgroupuser" />
		<input type="hidden" name="group" value="<mm:write referid="group" />">
		</form>
</table>
</mm:compare>

