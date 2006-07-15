<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">

<tr>
		<th>
		Date
		</th>
		<th>
		Status
		</th>
		<th>
		Type
		</th>
		<th>
		Commitor
		</th>
</tr>
<!-- the rest of the reports -->
<mm:relatednodes type="bugreportupdates" orderby="time" directions="down">
<tr>
		<td COLSPAN="1">
			<a href="showUpdate.jsp?updatereport=<mm:field name="number" />" target="update">
			<mm:field name="time" >
			<mm:time format="HH:mm:ss, EE d MM yyyy" />
			</mm:field>
			</a>
		</td>
		<td COLSPAN="1">
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</td>
		<mm:related path="rolerel,users" max="1">
		<td COLSPAN="1">
			<mm:field name="rolerel.role">
				<mm:compare value="submitter">Submitted</mm:compare>
				<mm:compare value="updater">Update</mm:compare>
			</mm:field>
		</td>
		<td COLSPAN="1">
			<a href="showUser.jsp?showuser=<mm:field name="users.number" />">
			<mm:field name="users.firstname" />
			<mm:field name="users.lastname" />
			</a>
		</mm:related>
		</td>
</tr>
</mm:relatednodes>
<!-- end of the reposts -->
</table>
