<center>
<table cellspacing="0" cellpadding="0" class="list" width="85%">
<form action="<mm:url referid="parameters,$parameters" page="/index.jsp?flap=mysettings" />" method="post">

<tr><th>Setting</th><th>Current Value</th></tr>
<mm:node referid="user">
<tr> 
   <th>Firstname</th><td><input name="firstname" value="<mm:field name="firstname" />" size="32" /></td>
</tr>
<tr>
 <th>Lastname</th><td><input name="lastname" value="<mm:field name="lastname" />" size="32" /></td>
</tr>
<tr>
  <th>email</th><td><input name="email" value="<mm:field name="email" />" size="32" />
		</td>
</tr>


<tr>
		<td colspan="3">
			<input type="hidden" name="action" value="updateuser">
			<center><b><input value="Save" type="submit" ></b>
		</td>
</tr>

</mm:node>
</form>
</table>

<mm:node referid="user">
<mm:relatednodes type="groups" constraints="name='BugTrackerAdmins'" max="1">
<table cellspacing="0" cellpadding="0" class="list" style="margin-top : 10px;" width="85%">

<tr>
		<th colspan="3">
			 Areas
		</th>
</tr>
<form action="<mm:url referids="parameters,$parameters" page"/index.jsp?flap=mysettings" />" method="post">
<tr>
		<th>
			Remove Area
		</th>
		<td>
			<select name="deletearea">
			<mm:node number="BugTracker.Start">
				<mm:relatednodes type="areas">
				<option value="<mm:field name="number" />"><mm:field name="name" />
				</mm:relatednodes>
			</mm:node>
			</select>
		</td>
		<td>
			<input type="hidden" name="action" value="removearea">
			<center><input value="Remove" type="submit" ></center>
		</td>
</tr>
</form>

<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings" method="post">
<tr>
		<th>
			Add Area
		</th>
		<td>
			<input name="newarea" value="" size="15">
		</td>
		<td>
			<input type="hidden" name="action" value="addarea">
			<center><input value="Add" type="submit" ></center>
		</td>
</tr>
</form>
</table>

<table cellspacing="0" cellpadding="0" class="list" width="85%" style="margin-top : 10px;">
<tr>
		<th colspan="3">
			 Commitors
		</th>
</tr>
<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings" method="post">
<tr>
		<th>
		Remove Commitor	
		</th>
		<td>
			<select name="deletecommitor">
			<mm:node number="BugTracker.commitors">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>
		</td>
		<td>
			<input type="hidden" name="action" value="removecommitor">
			<center><input value="Remove" type="submit" ></center>
		</td>
</tr>
</form>

<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings" method="post">
<tr>
		<th>
			Add Commitor
		</th>
		<td>
			<select name="newcommitor">
			<mm:node number="BugTracker.Interested">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>

		</td>
		<td>
			<input type="hidden" name="action" value="addcommitor">
			<center><input value="Add" type="submit" ></center>
		</td>
</tr>
</form>
</table>

<table width="85%" cellpadding="0" cellspacing="0" style="margin-top : 10px;" class="list">
<tr>
		<th colspan="3">
			 Admins
		</th>
</tr>
<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings" method="post">
<tr>
		<th>
		Remove Admin
		</th>
		<td>
			<select name="deleteadmin">
			<mm:node number="BugTracker.Admins">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>
		</td>
		<td>
			<input type="hidden" name="action" value="removeadmin">
			<center><input value="Remove" type="submit" ></center>
		</td>
</tr>
</form>

<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings" method="post">
<tr>
		<th>
			Add Admin
		</th>
		<td>
			<select name="newadmin">
			<mm:node number="BugTracker.Commitors">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>
		</td>
		<td>
			<input type="hidden" name="action" value="addadmin">
			<center><input value="Add" type="submit" >
		</td>
</tr>
</form>
</table>

</mm:relatednodes>
</mm:node>
