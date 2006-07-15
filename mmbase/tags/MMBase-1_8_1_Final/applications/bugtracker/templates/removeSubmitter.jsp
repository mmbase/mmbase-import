<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase Bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="bugreport" />
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#000000" ALINK="#555555" VLINK="#555555">
<BR>

<FORM ACTION="executes/removeSubmitter.jsp?bugreport=<mm:write referid="bugreport" />" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B> current submitters</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>remove Submitter</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Action</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='submitter'">
				<mm:field name="users.firstname" /> <mm:field name="users.lastname" />
			<BR>
			</mm:list>
				&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<SELECT NAME="submitterrel">
				<mm:list nodes="$bugreport" path="bugreports,rolerel,users" constraints="rolerel.role='submitter'">
				<OPTION VALUE="<mm:field name="rolerel.number"/>"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
				</mm:list>
				&nbsp;
			</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<INPUT TYPE="SUBMIT" VALUE="REMOVE">
		</TD>
</TR>
</TABLE>
</FORM>
</mm:cloud>
</BODY>
</HTML>
