<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase Bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="message">none</mm:import>
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#42BDAD" ALINK="#42BDAD" VLINK="#42BDAD">
<BR>
<FORM ACTION="index.jsp" METHOD="POST">
<center>
<TABLE cellspacing=1 cellpadding=10 border=0>
<TR>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>message
	</B>
	</TD>
</TR>

<TR>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<mm:compare referid="message" value="login">
			Login completed and browser linked (cookies) with this<br>
			account, press ok to return to the bugtracker.<br>	
		</mm:compare>
		<mm:compare referid="message" value="email">
			A account was indeed found with that email address<br>	
			mailed the account name and password to it.<br>	
		</mm:compare>
		<mm:compare referid="message" value="emailnotfound">
			No account found under that email address<br>
			maybe it was a different one ? or you don't have<br>
			a account yet ?<br>
		</mm:compare>
		<mm:compare referid="message" value="newuser">
			A account was created and password was mailed<br>	
			Check your mail and use the account info to login.<br>	
		</mm:compare>
		<mm:compare referid="message" value="reportdeleted">
			Bugreport was deleted from the database<br>
		</mm:compare>
		<mm:compare referid="message" value="newbug">
			The bug was inserted into the bugtracker, you<br>	
			are its submitter meaning you can change/delete<br>
			aspects of this report until its picked up by one<br>
			of the maintainers.<br><br>
			Thanks for reporting the bug we will report back to<br>
			you using email when its status is changed<br>

		</mm:compare>
		</TD>
</TR>
<TR>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<CENTER>
			<INPUT TYPE="SUBMIT" VALUE="ok">
		</TD>
</TR>
</TABLE>
</FORM>
</BODY>
</mm:cloud>
</HTML>
