<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="error">none</mm:import>
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#000000" ALINK="#555555" VLINK="#555555">
<BR>
<FORM ACTION="executes/newAccount.jsp" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD WIDTH="50"><IMG SRC="../beeld/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=2>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Create Account
	<mm:compare referid="error" value="email">
		<br><br><center>*** Email allready has a account, use resend account info !! ***</center><br><br>
	</mm:compare>
	<mm:compare referid="error" value="account">
		<br><br><center>*** Account name allready in use pick a new one ***</center><br><br>
	</mm:compare>
	<mm:compare referid="error" value="info">
		<br><br><center>*** Not all field where provided ***</center><br><br>
	</mm:compare>
	</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Account name	
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
        		<INPUT NAME="newaccount" VALUE="" SIZE="20">
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Firstname
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
        		<INPUT NAME="newfirstname" VALUE="" SIZE="20">
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Lastname
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
        		<INPUT NAME="newlastname" VALUE="" SIZE="20">
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		Email	
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
       			<INPUT NAME="newemail" VALUE="" SIZE="20">
		</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<BR>
			<CENTER>
			<INPUT TYPE="SUBMIT" VALUE="Create Account">
			<BR>
		</TD>
</TR>
</TABLE>
</FORM>
</mm:cloud>
</BODY>
</HTML>
