<table border="0" width="650">
<TR>
		<TD BGCOLOR="#00425A">
			 <br>
			 <br>
			  Bug : <font color="#ffffff">#<mm:field name="bugid" /></font>
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
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
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
			 <mm:field name="efixedin">
				<mm:compare value="" inverse="true">
			 		<mm:field name="efixedin" />
				</mm:compare>
				<mm:compare value="">
					Unknown
				</mm:compare>
			 </mm:field>
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
			<mm:relatednodes type="bugreportupdates" orderby="time" max="1">
				<mm:first>
					<br>
			 		Submitted : <font color="#ffffff">
					<mm:import id="found" />
				</mm:first>
				<mm:field name="time">
					<mm:time format="HH:mm:ss, EE d MM yyyy" />
				</mm:field>
				<mm:last>
				</font>
				</mm:last>
			</mm:relatednodes>
			<br>
			 <mm:present referid="found">Last update : </mm:present><mm:present referid="found" inverse="true">Submitted :</mm:present> <font color="#ffffff">
		<mm:field name="time">
			<mm:time format="HH:mm:ss, EE d MM yyyy" />
		</mm:field>
			</font>
		</TD>
</TR>


<TR>
		<TD BGCOLOR="#00425A" >
			<br>
				<mm:related path="rolerel,users" constraints="rolerel.role='maintainer'">
				<mm:first>
					<mm:import id="mfound">yes</mm:import>
				</mm:first>
				Maintainer :
<a href="showUser.jsp?showuser=<mm:field name="users.number" />"><font color="#ffffff"> <mm:field name="users.firstname" /> <mm:field name="users.lastname" /></font></a><br />
				</mm:related>
				<mm:present referid="mfound" inverse="true">
				Maintainer : <font color="#ffffff">none assigned</font> 
				</mm:present>
			  </font>
		</TD>
		<TD BGCOLOR="#00425A">
			<br>
			 Confirmed fixed in : <font color="#ffffff">
			 <mm:field name="fixedin">
				<mm:compare value="" inverse="true">
			 		<mm:field name="fixedin" />
				</mm:compare>
				<mm:compare value="">
					Unknown
				</mm:compare>
			 </mm:field>
			</font>
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
