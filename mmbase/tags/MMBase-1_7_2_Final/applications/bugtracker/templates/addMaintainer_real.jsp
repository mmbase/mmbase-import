<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="portal" />
<mm:import externid="page" />
<mm:import externid="bugreport" />

<center>
<form action="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />&flap=change" method="POST">
<table cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list" width="70%">
<tr>
	<th>
	current Maintainer
	</th>
	<th>
	New Maintainer
	</th>
	<th>
	Action
	</th>
</tr>

<tr>
		<td>
			<mm:list path="bugreports,rolerel,users" nodes="$bugreport" constraints="rolerel.role='maintainer'">
			<mm:field name="users.firstname" /> <mm:field name="users.lastname" /><br />
			</mm:list>
			&nbsp;
		</td>
		<td>
			<select name="maintainer">

				<mm:list path="users,groups" nodes="-1" constraints="groups.name='BugTrackerCommitors'">
				<option value="<mm:field name="users.number" />"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
				</mm:list>
				&nbsp;
			</select>
		</td>
		<td>
		<input type="hidden" name="action" value="addmaintainer" />
		<input type="SUBMIT" value="SAVE" />
		</td>
</tr>
</table>
</form>
</mm:cloud>
