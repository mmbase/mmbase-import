<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   RemoteProviderManager rpm = am.getRemoteProviderManager();
%>


<HTML>
<HEAD>
</HEAD>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3><BR>
<A HREF="../applications.jsp">Applications</A> / manage providers<BR><BR><BR>



<TABLE width=600 cellspacing=1 cellpadding=3 border=0>

<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="5">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Application Providers</B>
	</TD>
</TR>
	<!-- shows the components in the overview of a certain application -->



<!-- shows the header of the components listed in application.shtml -->
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<CENTER><B>Name</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<CENTER><B>Location</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<CENTER><B>Userid</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<CENTER><B>Password</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<CENTER><B>remove</B></CENTER>
	</TD>
</TR>



<!-- shows information about an element. It's used in application.shtml -->
<% for (Enumeration e = rpm.getRemoteProviders().elements();e.hasMoreElements();) { 
	RemoteProvider pr = (RemoteProvider)e.nextElement();
%>
<TR>
	
	<TD BGCOLOR="#00000">
	&nbsp;&nbsp;
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><%=pr.getName()%></FONT>
	&nbsp;&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=pr.getLocation()%>&nbsp;</CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=pr.getUserId()%>&nbsp;</CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><%=pr.getPassword()%>&nbsp;</CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><A HREF="deleteProvider.jsp?providername=<%=pr.getLocation()%>">delete</A></CENTER>
	</TD>
</TR>

<% } %>


<TR>
<FORM METHOD="POST" ACTION="addProvider.jsp">	
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>Specify provider</FONT>&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT type="TEXt" name="location">
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT type="TEXt" name="userid">
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT type="TEXt" name="password">
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT type="submit" value="Add">
	</TD>
	</FORM>
	</TR>
<TR>


</TABLE>
<BR><BR>
