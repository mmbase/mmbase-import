<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="bugreport" />
<mm:import externid="commenttype" />
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

<mm:node number="$bugreport">
<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#FFFFFF" ALINK="#555555" VLINK="#555555">
<BR>
<TABLE width="700" cellspacing=1 cellpadding=5 border=0>


<TR>
	<TD WIDTH="30"></TD>
	<TD BGCOLOR="42BDAD" WIDTH="630">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Adding a user comment</B>
	</TD>
</TR>
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" WIDTH="630" valign="top">
			<br />
			Commenting on bugreport :
			<font color="#ffffff"><mm:field name="issue" /></font>
			<BR>
			<BR>
			Commenting type :
			<font color="#ffffff"><mm:write referid="commenttype" /></font>
			<BR>
			<BR>
			<mm:compare referid="commenttype" value="regular">
			<form action="executes/addRegularComment.jsp?bugreport=<mm:write referid="bugreport" />&newuser=<mm:write referid="user" />" method="post">
			Title<br /> <INPUT NAME="newtitle" SIZE="60"><br />
			Text<br /> <TEXTAREA NAME="newtext" ROWS="25" COLS="58"></TEXTAREA>
			<center><input type="submit" value="enter comment"></center>
			</form>
			</mm:compare>
		</TD>
</TR>

</TABLE>
</mm:node>
</mm:cloud>
</BODY>
</HTML>
