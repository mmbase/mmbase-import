<table  cellpadding="0" cellspacing="0" class="list" width="97%">
<tr>
	<th width="50">
	Bug #
	</th>
	<th>
	state
	</th>
	<th>
	time
	</th>
	<th>
	issue
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<!-- the real searchpart -->


<mm:listnodes type="bugreports" orderby="time" directions="down" max="15" offset="$noffset">
<tr>
		<td>
			#<mm:field name="bugid" />
		</td>
		<td>
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</td>
		<td>
			<mm:field name="time">
				<mm:time format="HH:mm:ss, EE d MM yyyy" />
			</mm:field>
		</td>
		<td>
			<mm:field name="issue" />
		</td>
		<td>
			<A HREF="fullview.jsp?bugreport=<mm:field name="number" />&portal=<mm:write referid="portal" />&page=<mm:write referid="page" />"><IMG SRC="images/arrow-right.gif" BORDER="0" ALIGN="middle"></A>
		</td>
</tr>
</mm:listnodes>
</tr>
</table>

<!-- end of the searchpart -->

<center>
<table cellspacing="0" cellpadding="0" align="middle" width="80%">
<tr>

		<mm:present referid="user" inverse="true" >
			<td>
			 <center><font color="#000000">We have no idea who you are please login !<A HREF="changeUser.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />"><IMG SRC="images/arrow-right.gif" border="0" valign="middle"></A></font>
			</td>
		</mm:present>
		<mm:present referid="user">
			<td colspan="1">
			<br />
			<mm:node number="$user">
			<center> <font color="black">I am <mm:field name="firstname" /> <mm:field name="lastname" /> ( its not me , <A HREF="changeUser.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />">change name</A> )<br /> i have a new bug and want to report it</font><A HREF="newBug.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&user=<mm:write referid="user" />"><IMG SRC="images/arrow-right.gif" BORDER="0" ></A>
			</td>
			</mm:node>
		</mm:present>
</tr>
</table>
</center>
</form>

