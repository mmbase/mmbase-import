<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="bugreport" />
<mm:import externid="portal" />
<mm:import externid="page" />

<form action="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:write referid="bugreport" />&flap=change" method="POST">
<center>
<table cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list" width="70%">
<tr>
	<th>
	current Maintainer
	</th>
	<th>
	remove Maintainer
	</th>
	<th>
	Action
	</th>
</tr>

<tr>
		<td>
			<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='maintainer'">
				<mm:field name="users.firstname" /> <mm:field name="users.lastname" />
			<BR>
			</mm:list>
				&nbsp;
		</td>
		<td>
			<select name="maintainerrel">
				<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='maintainer'">
				<OPTION VALUE="<mm:field name="rolerel.number"/>"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
				</mm:list>
				&nbsp;
			</select>
		</td>
		<td>
		<input type="hidden" name="action" value="removemaintainer" />
		<input type="SUBMIT" value="REMOVE" />
		</td>
</tr>
</table>
</center>
</form>
</mm:cloud>
