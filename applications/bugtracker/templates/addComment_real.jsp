<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="portal" />
<mm:import externid="page" />
<mm:import externid="bugreport" />
<mm:import externid="commenttype" />
<mm:import externid="cw" from="cookie" />
<mm:import externid="ca" from="cookie" />
<mm:present referid="ca">
        <mm:present referid="cw">
			<mm:listnodes type="users" constraints="account='$ca' and password='$cw'" max="1">
				<mm:import id="user"><mm:field name="number" /></mm:import>
			</mm:listnodes>
        </mm:present>
</mm:present>
<mm:present referid="user">
	<mm:list path="users,groups" nodes="$user" constraints="groups.name='BugTrackerCommitors'" max="1">
				<mm:import id="commitor"><mm:field name="users.number" /></mm:import>
	</mm:list>

<mm:node number="$bugreport">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">


<tr>
	<th>
	Adding a user comment
	</th>
</tr>
<tr>
		<td valign="top">
			Commenting on bugreport :
			<p />
			<mm:field name="issue" />
			<p />
			Commenting type :
			<mm:write referid="commenttype" />
			<p />
			<mm:compare referid="commenttype" value="regular">
			<form action="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=comments&bugreport=<mm:write referid="bugreport" />&newuser=<mm:write referid="user" />" method="post">
			Title<br /> <input name="newtitle" style="width: 100%" /><br />
			Text<br /> <textarea name="newtext" rows="25" style="width: 100%"></textarea>
			<input type="hidden" name="action" value="addcomment" />
			<center><input type="submit" value="enter comment"></center>
			</form>
			</mm:compare>
		</td>
</tr>

</table>
</mm:node>
</mm:present>
</mm:cloud>
</body>
</html>
