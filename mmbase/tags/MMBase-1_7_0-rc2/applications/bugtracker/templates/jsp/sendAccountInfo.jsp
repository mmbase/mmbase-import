<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="error">none</mm:import>
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#42BDAD" ALINK="#42BDAD" VLINK="#42BDAD">
<BR>
<FORM ACTION="executes/sendAccountInfo.jsp" METHOD="POST">
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
		Please give us your email so we can resend the info
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="email" VALUE="" SIZE="25"><BR>
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<CENTER>
			<INPUT TYPE="SUBMIT" VALUE="Send info">
			<BR>
			<BR>
		</TD>
</TR>
</TABLE>
</FORM>
</BODY>
</mm:cloud>
</HTML>
