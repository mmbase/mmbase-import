<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="user" />
<mm:node number="$user">
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#000000" ALINK="#555555" VLINK="#555555">
<BR>

<FORM ACTION="executes/newBug.jsp" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>

<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B> Type</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Priority</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Version</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<SELECT NAME="newbtype">
				<OPTION VALUE="1">bug
				<OPTION VALUE="2">wish
				<OPTION VALUE="3">docbug
				<OPTION VALUE="4">docwish
			</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<SELECT NAME="newbpriority">
				<OPTION VALUE="1">high
				<OPTION VALUE="2" SELECTED>medium
				<OPTION VALUE="3">low
			</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="newversion" VALUE="1.6.1" SIZE="10">
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	


<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Area</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>&nbsp;</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>&nbsp;</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<mm:import id="noareas" />
		<mm:node number="BugTracker.Start">
		<SELECT NAME="newarea">
			<mm:relatednodes type="areas">
			<mm:first><mm:remove referid="noareas" /></mm:first>
			<OPTION VALUE="<mm:field name="number" />"
			<mm:field name="name">
			<mm:compare value="Misc">SELECTED</mm:compare>
			</mm:field>
			><mm:field name="substring(name,15,.)" />
			</mm:relatednodes>
		</SELECT>
		</mm:node>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		&nbsp;
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	

<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=3>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Issue : give the issue in one line </B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			&nbsp;&nbsp;<INPUT SIZE="50" NAME="newissue">
		</TD>
</TR>


<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=3>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Description : Describe the issue as complete as possible </B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="3">
			<TEXTAREA NAME="newdescription" COLS="50" ROWS="15" WRAP></TEXTAREA>
		</TD>
</TR>


<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=2>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Submitter</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	&nbsp;	
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<INPUT NAME="submitter" TYPE="hidden" VALUE="<mm:field name="number" />">
			&nbsp;&nbsp;
			<mm:field name="firstname" />
			<mm:field name="lastname" />
			 ( <mm:field name="email" /> )
			
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<CENTER>
			<mm:present referid="noareas" inverse="true">
				<INPUT TYPE="submit" VALUE="SUBMIT REPORT">
			</mm:present>
			<mm:present referid="noareas">
				No areas defined, admin needs to add areas !
			</mm:present>
		</TD>
</TR>

</TABLE>
</mm:node>
</mm:cloud>
</FORM>
</BODY>
</HTML>
