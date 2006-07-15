<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="updatereport" />
<mm:import externid="cw" from="cookie" />
<mm:import externid="ca" from="cookie" />
<mm:present referid="ca">
        <mm:present referid="cw">
			<mm:listnodes type="users" constraints="account='$ca' and password='$cw'" max="1">
				<mm:import id="user"><mm:field name="number" /></mm:import>
			</mm:listnodes>
        </mm:present>
</mm:present>
<mm:present referid="user">
	<mm:list path="users,groups" nodes="$user" constraints="groups.name='BugTrackerCommitors'" max="1">
				<mm:import id="commitor"><mm:field name="users.number" /></mm:import>
	</mm:list>
</mm:present>

<mm:node number="$updatereport">
<mm:related path="rolerel,users" constraints="rolerel.role='maintainer'" max="1">
	<mm:import id="hasmaintainers">yes</mm:import>
</mm:related>
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#FFFFFF" ALINK="#555555" VLINK="#555555">
<BR>
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>


<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Bug #</B>
	</TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Status</B>
	</TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Type</B>
	</TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Priority</B>
	</TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Version</B>
	</TD>
	<TD BGCOLOR="42BDAD">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Issue</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A">
			  #<mm:field name="number" />
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			<mm:field name="version" />&nbsp;
		</TD>
		<TD BGCOLOR="#00425A">
			<mm:field name="issue" />
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;
	</TD>
</TR>


<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=6>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Description of the issue</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="6">
			<mm:field name="html(description)" />
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
	<TD BGCOLOR="42BDAD" COLSPAN=6>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Rational from maintainer on state & priority</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="6">
			<mm:field name="html(rationale)" />
			&nbsp;
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;
	</TD>
</TR>



</TABLE>
</mm:node>
</mm:cloud>
</BODY>
</HTML>
