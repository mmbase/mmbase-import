<mm:node number="<%= personId %>"
><table cellpadding="0" cellspacing="0" class="relationcontainer" style="width:100%;">
<tr><th colspan="2"><table cellpadding="0" cellspacing="0" class="relationcontainer" style="width:100%;">
<tr>
	<th><mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/></th>
	<th><%@include file="/includes/images.jsp" %></th>
</tr></table></th></tr>
<tr><td>nickname</td><td><mm:field name="nickname" />&nbsp;</td></tr>
<mm:related path="organisation"
	><mm:first><tr><td>organisation</td><td></mm:first
	><mm:first inverse="true">, </mm:first
	><mm:field name="organisation.name" 
	/><mm:last></td></tr></mm:last
></mm:related>
<mm:related path="urls"
	><mm:first><tr><td>homepage</td><td></mm:first
	><mm:first inverse="true">, </mm:first
	><a href="<mm:field name="urls.url" />" target="_blank"><mm:field name="urls.name"
			><mm:isempty><mm:field name="urls.url" /></mm:isempty
			><mm:isnotempty><mm:write /></mm:isnotempty
		></mm:field></a><mm:last
	></td></tr></mm:last
></mm:related>
<tr><td>address</td><td><mm:field name="address" />&nbsp;</td></tr>
<tr><td></td><td><mm:field name="zipcode" />&nbsp;<mm:field name="city" /></td></tr>
<tr><td></td><td><mm:field name="country" />&nbsp;</td></tr>
<tr><td>company&nbsp;phone</td><td><mm:field name="companyphone" />&nbsp;</td></tr>
<tr><td>cellular&nbsp;phone</td><td><mm:field name="cellularphone" />&nbsp;</td></tr>
<tr><td>email</td><td><a href="mailto:<mm:field name="email"/>"><mm:field name="email" /></a>&nbsp;</td></tr>
<mm:related path="project"
	><mm:first><tr><td>project</td><td></mm:first
	><mm:first inverse="true">, </mm:first
	><mm:field name="project.title" 
	/><mm:last></td></tr></mm:last
></mm:related>
<tr><td>comments</td><td><mm:field name="comments" />&nbsp;</td></tr>
<mm:node number="<%= personId %>"
	><mm:import id="email"><mm:field name="email" /></mm:import
	><mm:listnodes type="users" constraints="email='$email'" max="1">
	<tr><form method="POST" name="peoplefinder" action="<mm:url referids="portal,page" />">
		<td>login to update your info (you can use your bugtracker password)</td>
		<td><input type="text" name="pwd" size="13" value="your password" onClick="this.value='';">
		<input type="hidden" name="person" value="<%= personId %>"> 
		<input type="submit" name="login" value="Login" style="text-align:center;font-weight:bold;">&nbsp;</td>
	</form></tr>
	<mm:import id="userfound"
	/></mm:listnodes
	><mm:notpresent referid="userfound">
	<tr><form method="POST" name="peoplefinder" action="<mm:url referids="portal,page" />">
		<td>create an account to update your info</td>
		<td><input type="hidden" name="person" value="<%= personId %>"> 
		<input type="submit" name="login" value="Create account" style="text-align:center;font-weight:bold;">&nbsp;</td>
	</form></tr>
	</mm:notpresent
></mm:node>
</table>
</mm:node>
