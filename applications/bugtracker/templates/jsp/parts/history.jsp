<table border="0" width="650">

<TR>
		<TD BGCOLOR="#00425A">
		<br>
		<font color="#ffffff">Date</font>
		</TD>
		<TD BGCOLOR="#00425A">
		<br>
		<font color="#ffffff">Status</font>
		</TD>
		<TD BGCOLOR="#00425A"">
		<br>
		<font color="#ffffff">Type</font>
		</TD>
		<TD BGCOLOR="#00425A">
		<br>
		<font color="#ffffff">Commitor</font>
		</TD>
</TR>
<!-- the rest of the reports -->
<mm:relatednodes type="bugreportupdates" orderby="time" directions="down">
<TR>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<a href="showUpdate.jsp?updatereport=<mm:field name="number" />" target="update">
			<font color="#42BDAD">
			<mm:field name="time" >
			<mm:time format="HH:mm:ss, EE d MM yyyy" />
			</mm:field>
			</font>
			</a>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</TD>
		<mm:related path="rolerel,users" max="1">
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:field name="rolerel.role">
				<mm:compare value="submitter">Submitted</mm:compare>
				<mm:compare value="updater">Update</mm:compare>
			</mm:field>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<a href="showUser.jsp?showuser=<mm:field name="users.number" />">
			<font color="#42BDAD">
			<mm:field name="users.firstname" />
			<mm:field name="users.lastname" />
			</font>
			</a>
		</mm:related>
		</TD>
</TR>
</mm:relatednodes>
<!-- end of the reposts -->
</table>
