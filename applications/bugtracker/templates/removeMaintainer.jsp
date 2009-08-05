<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>
<mm:import externid="bugreport" required="true"/>

<form action="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate">fullview.jsp</mm:param><mm:param name="flap">change</mm:param></mm:url>" method="POST">
<center>
<table cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list" width="70%">
<tr>
	<th>
	current Maintainer
	</th>
	<th>
	remove Maintainer
	</th>
	<th>
	Action
	</th>
</tr>

<tr>
		<td>
			<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='maintainer'">
				<mm:field name="users.firstname" /> <mm:field name="users.lastname" />
			<BR>
			</mm:list>
				&nbsp;
		</td>
		<td>
			<select name="maintainerrel">
				<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='maintainer'">
				<OPTION VALUE="<mm:field name="rolerel.number"/>"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
				</mm:list>
				&nbsp;
			</select>
		</td>
		<td>
		<input type="hidden" name="action" value="removemaintainer" />
		<input type="SUBMIT" value="REMOVE" />
		</td>
</tr>
</table>
</center>
</form>
</mm:cloud>
