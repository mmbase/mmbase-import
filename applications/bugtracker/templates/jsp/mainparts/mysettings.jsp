<table border="0" width="100%">
<TR>
	<TD>
	<br />
	</TD>
</TR>
</TABLE>
<table border="0" width="100%" cellpadding="2">
<form action="executes/setUserInfo.jsp?usernumber=<mm:write referid="user" />" method="post">

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <B><FONT COLOR="#000000">Setting</FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000">Current Value</FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"></FONT></B>
		</TD>
</TR>
<mm:node referid="user">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Firstname
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<input name="firstname" value="<mm:field name="firstname" />" size="15">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			&nbsp;
		</TD>
</TR>

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Lastname	
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<input name="lastname" value="<mm:field name="lastname" />" size="15">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			&nbsp;
		</TD>
</TR>


<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Account
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<input name="account" value="<mm:field name="account" />" size="15">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			&nbsp;
		</TD>
</TR>


<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Save" type="submit" ></b>
		</TD>
</TR>

</mm:node>
</form>
</table>

<TR>
	<TD>
	<br />
	</TD>
</TR>

<mm:node referid="user">
<mm:relatednodes type="groups" constraints="name='BugTrackerAdmins'" max="1">
<table border="0" width="100%" cellpadding="2">

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <B><FONT COLOR="#000000">Areas</FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"> </FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"></FONT></B>
		</TD>
</TR>
<form action="executes/removeArea.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Remove Area
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<select name="deletearea">
			<mm:node number="BugTracker.Start">
				<mm:relatednodes type="areas">
				<option value="<mm:field name="number" />"><mm:field name="name" />
				</mm:relatednodes>
			</mm:node>
			</select>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Remove" type="submit" ></b>
		</TD>
</TR>
</form>

<form action="executes/addArea.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Add Area
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<input name="newarea" value="" size="15">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Add" type="submit" ></b>
		</TD>
</TR>
</form>
</table>

<TR>
	<TD>
	<br />
	</TD>
</TR>


<table border="0" width="100%" cellpadding="2">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <B><FONT COLOR="#000000">Commitors</FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"> </FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"></FONT></B>
		</TD>
</TR>
<form action="executes/removeCommitor.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
		Remove Commitor	
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<select name="deletecommitor">
			<mm:node number="BugTracker.commitors">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Remove" type="submit" ></b>
		</TD>
</TR>
</form>

<form action="executes/addCommitor.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Add Commitor
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<select name="newcommitor">
			<mm:node number="BugTracker.Interested">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>

		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Add" type="submit" ></b>
		</TD>
</TR>
</form>
</table>


<TR>
	<TD>
	<br />
	</TD>
</TR>


<table border="0" width="100%" cellpadding="2">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <B><FONT COLOR="#000000">Admins</FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"> </FONT></B>
		</TD>
		<TD BGCOLOR="#42BDAD" COLSPAN=3">
			 <B><FONT COLOR="#000000"></FONT></B>
		</TD>
</TR>
<form action="executes/removeAdmin.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
		Remove Admin
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<select name="deleteadmin">
			<mm:node number="BugTracker.Admins">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Remove" type="submit" ></b>
		</TD>
</TR>
</form>

<form action="executes/addAdmin.jsp" method="post">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			Add Admin
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<select name="newadmin">
			<mm:node number="BugTracker.Commitors">
			<mm:relatednodes type="users">
				<option value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)
			</mm:relatednodes>
			</mm:node>
			</select>

		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<center><b><input value="Add" type="submit" ></b>
		</TD>
</TR>
</form>
</table>

</mm:relatednodes>
</mm:node>
