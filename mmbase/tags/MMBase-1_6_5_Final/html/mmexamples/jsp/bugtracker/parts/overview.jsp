<table border="0" width="650">
<TR>
		<TD BGCOLOR="#00425A">
			 <br>
			 <br>
			  Bug : <font color="#ffffff">#<mm:field name="number" /></font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br><br>
			Issue : <font color="#ffffff"><mm:field name="issue" /></font>
		</TD>
</TR>

<TR>
		<TD BGCOLOR="#00425A">
			<br>
			  Status : <font color="#ffffff">
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
			<mm:relatednodes type="areas">
			Area : <font color="#ffffff"><mm:field name="name" /></font>
			</mm:relatednodes>
		</TD>
</TR>


<TR>
		<TD BGCOLOR="#00425A">
			<br>
			  Type : <font color="#ffffff">
			 <mm:field name="btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Enhanchement</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocEnhanchement</mm:compare>
			 </mm:field>
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
			Priority : <font color="#ffffff">
			 <mm:field name="bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
			</font>
		</TD>
</TR>

<TR>
		<TD BGCOLOR="#00425A">
			<br>
			  Version : <font color="#ffffff">
			 <mm:field name="version" />
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
			 Expected fixed in : <font color="#ffffff">
			 1.6.0
			</font>
		</TD>
</TR>


<TR>
		<TD BGCOLOR="#00425A">
			<br>
			  Submitter : <font color="#ffffff">
				<mm:related path="rolerel,users" max="1" constraints="rolerel.role='submitter'">
				<a href="showUser.jsp?showuser=<mm:field name="users.number" />"><font color="#ffffff"> <mm:field name="users.firstname" /> <mm:field name="users.lastname" /></font></a>
				</mm:related>
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
			 Submitted : <font color="#ffffff">
		<mm:field name="time(time)" /> on
		<mm:field name="weekday(time)" /> 
		<mm:field name="day(time)" />
		<mm:field name="month(time)" />
		<mm:field name="year(time)" />
			</font>
		</TD>
</TR>


<TR>
		<TD BGCOLOR="#00425A" >
			<br>
			  Maintainer : <font color="#ffffff">
				<mm:related path="rolerel,users" max="1" constraints="rolerel.role='maintainer'">
				<a href="showUser.jsp?showuser=<mm:field name="users.number" />"><font color="#ffffff"> <mm:field name="users.firstname" /> <mm:field name="users.lastname" /></font></a>
				</mm:related>
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
		</TD>
</TR>


<TR>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<br>
			  Longer bug description <font color="#ffffff"><br><br>
				<mm:field name="html(description)" />
			  </font>
		</TD>
</TR>
</table>
