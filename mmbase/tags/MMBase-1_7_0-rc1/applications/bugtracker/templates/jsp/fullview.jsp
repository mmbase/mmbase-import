<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<HTML>
<HEAD>
   <TITLE>MMBase bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="bugreport" />
<mm:import externid="flap" jspvar="flap">overview</mm:import>
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
<mm:related path="rolerel,users" constraints="rolerel.role='maintainer'" max="1">
	<mm:import id="hasmaintainers">yes</mm:import>
</mm:related>


<mm:related path="rolerel,users" constraints="rolerel.role='submitter'">
	<mm:import id="submitter"><mm:field name="users.number" /></mm:import>
</mm:related>


<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#00425b" ALINK="#00425b" VLINK="#00425B">
<BR>
<TABLE width="700" cellspacing=1 cellpadding=3 border=0>

<TR>
		<TD WIDTH="50"></TD>
		<TD BGCOLOR="#44BDAD">
			<A HREF="index.jsp"><IMG SRC="images/arrow2.gif" BORDER="0" ALIGN="left"></A>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="4">
		&nbsp;
		</TD>
</TR>

<TR>
	<TD>
	&nbsp;
	</TD>
</TR>

<TR>
	<TD WIDTH="50"></TD>
	<%@ include file="parts/flaps.jsp" %>
</TR>




<TR>
		<TD>
		&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="5">
		<mm:compare referid="flap" value="overview"><%@ include file="parts/overview.jsp" %> </mm:compare>
		<mm:compare referid="flap" value="comments"><%@ include file="parts/comments.jsp" %> </mm:compare>
		<mm:compare referid="flap" value="history"><%@ include file="parts/history.jsp" %> </mm:compare>
		<mm:compare referid="flap" value="change"><%@ include file="parts/change.jsp" %> </mm:compare>
		<mm:compare referid="flap" value="mybug"><%@ include file="parts/mybug.jsp" %> </mm:compare>
		&nbsp;
		</TD>
</TR>
</TABLE>
</mm:node>
</mm:cloud>
</BODY>
</HTML>
