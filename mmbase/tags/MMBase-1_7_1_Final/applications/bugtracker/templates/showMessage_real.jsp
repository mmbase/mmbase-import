<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@include file="actions.jsp" %>


<mm:notpresent referid="message">
<mm:import externid="message"/>
</mm:notpresent>
<form action="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />" method="POST">
<center>
<table cellspacing="0" cellpadding="0" style="margin-top : 70px;" class="list" width="70%">
<tr>
	<th>
	feedback message
	</th>
</tr>

<tr>
		<td>
		<p />
		<mm:compare referid="message" value="login">
			Login completed and browser linked (cookies) with this
			account, press ok to return to the bugtracker.
		</mm:compare>
		<mm:compare referid="message" value="email">
			A account was indeed found with that email address	
			mailed the account name and password to it.
		</mm:compare>
		<mm:compare referid="message" value="emailnotfound">
			No account found under that email address
			maybe it was a different one ? or you don't have
			a account yet ?
		</mm:compare>
		<mm:compare referid="message" value="newuser">
			A account was created and password was mailed
			Check your mail and use the account info to login.
		</mm:compare>
		<mm:compare referid="message" value="reportdeleted">
			Bugreport was deleted from the database
		</mm:compare>
		<mm:compare referid="message" value="newbug">
			The bug was inserted into the bugtracker, you
			are its submitter meaning you can change/delete
			aspects of this report until its picked up by one
			of the maintainers.
			Thanks for reporting the bug we will report back to
			you using email when its status is changed

		</mm:compare>
		<mm:compare referid="message" value="updatebug">
			The bug was updated into the bugtracker. 
			Thanks for reporting the change we will report back to
			you using email when its status is changed

		</mm:compare>
		<p />
		</td>
</tr>
<tr>
		<td>
			<center>
			<INPUT TYPE="SUBMIT" VALUE="ok">
		</td>
</tr>
</table>
</form>
</mm:cloud>
