<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="showuser" />
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

<mm:node number="$showuser">
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#FFFFFF" ALINK="#555555" VLINK="#555555">
<BR>
<TABLE width="700" cellspacing=1 cellpadding=3 border=0>


<TR>
	<TD WIDTH="30"></TD>
	<TD BGCOLOR="42BDAD" WIDTH="670" COLSPAN="2">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>User</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" WIDTH="370" valign="top">
			<br>
			<br>
			Name :
			<font color="#ffffff"><mm:field name="firstname" />
			<mm:field name="lastname" />
			</font>
			<BR>
			<BR>
			Bio :
			<mm:relatednodes type="biographies">
			<font color="#ffffff"><mm:field name="html(intro)" />
			</font>
			</mm:relatednodes>
			<BR>
			<BR>
			Active user : 
			<mm:relatednodes type="groups" constraints="name='BugTrackerInterested'">
			<font color="#ffffff">Yes</font>
			</mm:relatednodes>
			<BR>
			<BR>
			Commitor : 
			<mm:relatednodes type="groups" constraints="name='BugTrackerCommitors'">
			<font color="#ffffff">Yes</font>
			<mm:import id="usercommitor">yes</mm:import>
			</mm:relatednodes>
			<mm:present referid="usercommitor" inverse="true">
			<font color="#ffffff">No</font>
			</mm:present>
			

		</TD>
		<TD BGCOLOR="#00425A" WIDTH="300">
			<mm:relatednodes type="images">
			Photo: <IMG SRC="/img.db?<mm:field name="number" />+s(90)" valign="top" ><br><br>
			</mm:relatednodes>
			Email : <a href="mailto:<mm:field name="email" />"><mm:field name="email" /></a>
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;
	</TD>
</TR>


<TR>
	<TD WIDTH="30"></TD>
	<TD BGCOLOR="42BDAD" WIDTH="670" COLSPAN="2">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Last bugs he was active on</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN=2>
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
