<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">

<%@ page contentType="text/html; charset=utf-8" language="java" %>
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="user" />
<mm:import externid="bugreport" />
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#000000" ALINK="#555555" VLINK="#555555">
<BR>

<FORM ACTION="executes/updateBugreport.jsp" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>

<mm:node number="$bugreport">
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
	<B>Status</B>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>&nbsp;</B>
	</TD>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:field name="btype">
			<SELECT NAME="newbtype">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>bug
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>wish
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>docbug
				<OPTION VALUE="4" <mm:compare value="4">SELECTED</mm:compare>>docwish
			</SELECT>
			</mm:field>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:field name="bpriority">
			<SELECT NAME="newbpriority">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>high
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>medium
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>low
			</SELECT>
			</mm:field>
		</TD>

		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:field name="bstatus">
			<SELECT NAME="newbstatus">
				<OPTION VALUE="1" <mm:compare value="1">SELECTED</mm:compare>>open
				<OPTION VALUE="2" <mm:compare value="2">SELECTED</mm:compare>>accepted
				<OPTION VALUE="3" <mm:compare value="3">SELECTED</mm:compare>>rejected
				<OPTION VALUE="4" <mm:compare value="4">SELECTED</mm:compare>>pending
				<OPTION VALUE="5" <mm:compare value="5">SELECTED</mm:compare>>integrated
				<OPTION VALUE="6" <mm:compare value="6">SELECTED</mm:compare>>closed
			</SELECT>
			</mm:field>
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
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Version</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Area</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Expected fixed in</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Fixed in</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="newversion" value="<mm:field name="version" />" SIZE="10">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<SELECT NAME="newarea">
			<mm:relatednodes type="areas" max="1">
				<OPTION VALUE="<mm:field name="number" />"><mm:field name="substring(name,15,.)" />
			</mm:relatednodes>
			<mm:listnodes type="areas" orderby="name" >
			<OPTION VALUE="<mm:field name="number" />">
			<mm:field name="substring(name,15,.)" />
			</mm:listnodes>
		</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="newefixedin" value="<mm:field name="efixedin" />" SIZE="10">
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<INPUT NAME="newfixedin" value="<mm:field name="fixedin" />" SIZE="10">
		</TD>
</TR>
<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=5>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Issue : give the issue in one line </B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="5">
			&nbsp;&nbsp;<INPUT SIZE="70" NAME="newissue" value="<mm:field name="issue" />" >
		</TD>
</TR>


<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>	
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=4>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Description : Describe the issue as complete as possible </B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="5">
			<TEXTAREA NAME="newdescription" COLS="70" ROWS="15" WRAP><mm:field name="description" /></TEXTAREA>
		</TD>
</TR>


<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN="5">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Rationale : explains the actions made by the maintainer </B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="5">
			<TEXTAREA NAME="newrationale" COLS="70" ROWS="15" WRAP><mm:field name="rationale" /></TEXTAREA>
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
	<TD BGCOLOR="42BDAD" COLSPAN=2>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	&nbsp;	
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			
			<mm:related path="insrel,areas" max="1">
			<INPUT NAME="oldarea" TYPE="hidden" VALUE="<mm:field name="number.areas" />">
			<INPUT NAME="oldarearel" TYPE="hidden" VALUE="<mm:field name="insrel.number" />">
			</mm:related>
</mm:node>
			<mm:node referid="user">
			<INPUT NAME="updater" TYPE="hidden" VALUE="<mm:field name="number" />">
			<INPUT NAME="bugreport" TYPE="hidden" VALUE="<mm:write referid="bugreport" />">
			&nbsp;&nbsp;
			<mm:field name="firstname" />
			<mm:field name="lastname" />
			 ( <mm:field name="email" /> )
			</mm:node>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="2">
			<CENTER><INPUT TYPE="submit" VALUE="SUBMIT UPDATE">
		</TD>
</TR>

</TABLE>
</mm:cloud>
</FORM>
</BODY>
</HTML>
