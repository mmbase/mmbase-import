<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase Bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="bugreport" />
<mm:node number="$bugreport">

<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#000000" ALINK="#555555" VLINK="#555555">
<BR>

<FORM ACTION="executes/changePriority.jsp?bugreport=<mm:write referid="bugreport" />" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Current priority</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>New priority</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Action</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:field name="bpriority">
				<mm:compare value="1">high</mm:compare>
				<mm:compare value="2">medium</mm:compare>
				<mm:compare value="3">low</mm:compare>
			</mm:field>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<SELECT NAME="newpriority">
			<mm:field name="bpriority">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>high
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>medium
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>low
			</mm:field>
			</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<INPUT TYPE="SUBMIT" VALUE="SAVE">
		</TD>
</TR>
</TABLE>
</FORM>
</mm:node>
</mm:cloud>
</BODY>
</HTML>
