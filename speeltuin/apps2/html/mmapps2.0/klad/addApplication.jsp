<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Hashtable localApps = am.getLocalApplications();
   Hashtable remoteApps = am.getRemoteApplications();
%>

<HTML>
<HEAD>
	<TITLE>Application 2.0 - Adding applications</TITLE>
</HEAD>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3><BR>
<A HREF="../applications.jsp">Applications</A> / add Applications<BR><BR><BR>


<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="4">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Adding an application</B>
	</TD>
</TR>

<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Name</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Version</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Provider</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Description</B></CENTER>
	</TD>
</TR>

<% for (Enumeration e = localApps.elements();e.hasMoreElements();) { 
   AvailableApplication av = (AvailableApplication)e.nextElement();
%>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getName()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getConfigurationVersion()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getLocation()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getDescription()%>&nbsp;
	</TD>
</TR>
<% } %>

<% for (Enumeration e = remoteApps.elements();e.hasMoreElements();) { 
   AvailableApplication av = (AvailableApplication)e.nextElement();
%>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getName()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getConfigurationVersion()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getLocation()%>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=av.getDescription()%>&nbsp;
	</TD>
</TR>
<% } %>

</TABLE>
<BR><BR>
