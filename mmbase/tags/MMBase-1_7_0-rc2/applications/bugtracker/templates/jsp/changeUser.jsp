<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase Bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="error">none</mm:import>
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#42BDAD" ALINK="#42BDAD" VLINK="#42BDAD">
<BR>
<FORM ACTION="executes/checkUser.jsp" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=2>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>login
		<mm:compare referid="error" value="login">
			<br><br>
			<center>** wrong account or password **</center>
		</mm:compare>
	</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Login
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="account" VALUE="" SIZE="10"><BR>
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Password
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="password" VALUE="" SIZE="10" TYPE="password"></FONT>
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<CENTER>
			<INPUT TYPE="SUBMIT" VALUE="Login">
			<BR>
			<BR>
forgot my password mail me the info <A HREF="sendAccountInfo.jsp">resend info</A><br>
I don't have a password yet <A HREF="newUser.jsp">get account</A>
		</TD>
</TR>
</TABLE>
</FORM>
</BODY>
</mm:cloud>
</HTML>
