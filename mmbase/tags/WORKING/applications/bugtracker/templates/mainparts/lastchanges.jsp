<table  cellpadding="0" cellspacing="0" class="list" width="97%">

<tr><th width="50">Bug #</th><th>state</th><th>time</th><th>issue</th><th>&nbsp;</th></tr>
<!-- the real searchpart -->


<mm:listnodes type="bugreports" orderby="time" directions="down" max="15" offset="$noffset">
<tr>
    <td>#<mm:field name="bugid" /></td>
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
			<a href="<mm:url referids="portal?,page?" page="$base/fullview.jsp"><mm:param name="bugreport"><mm:field name="number" /></mm:param></mm:url>"><img src="<mm:url page="$base/images/arrow-right.gif" />" border="0" align="middle"></a>
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
			 <center><font color="#000000">We have no idea who you are please login !<a href="<mm:url referids="portal?,page?" page="$base/changeUser.jsp" />"><img src="<mm:url page="$base/images/arrow-right.gif" />" border="0" valign="middle"></a></font>
			</td>
		</mm:present>
		<mm:present referid="user">
			<td colspan="1">
			<br />
			<mm:node number="$user">
			<center> <font color="black">I am <mm:field name="firstname" /> <mm:field name="lastname" /> ( its not me , <a href="<mm:url referids="portal?,page?" page="$base/changeUser.jsp" />">change name</a> )<br /> i have a new bug and want to report it</font><a href="<mm:url page="$base/newBug.jsp" referids="portal?,page?,user" />"><img src="<mm:url page="$base/images/arrow-right.gif" />" border="0" ></a>
			</td>
			</mm:node>
		</mm:present>
</tr>
</table>
</center>
</form>

