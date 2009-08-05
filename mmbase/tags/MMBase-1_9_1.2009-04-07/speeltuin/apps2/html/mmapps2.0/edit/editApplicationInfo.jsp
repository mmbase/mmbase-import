<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   String appname=request.getParameter("appname");
   Application app = am.getApplication(appname);
%>
<HTML>
<HEAD>
<TITLE>
Applications 2.0 - Edit Application Information <%=appname%>
</TITLE>
</HEAD>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3><BR>
<A HREF="../applications.jsp">Applications</A> / 
<A HREF="editComponents.jsp?appname=<%=appname%>"><%=appname%> Components</A> / 
<%=appname%> Information<BR><BR><BR>

<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="4">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Editing information of application <%=appname%></B>
	</TD>
</TR>
<FORM METHOD="POST" ACTION="processApplicationInfo.shtml?<%=appname%>">
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Applicationname</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" NAME="SESSION-name" 
	<% if(app!=null) { %> VALUE="<%=app.getName()%>" <% } %> >
	</TD>
</TR>

<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Description</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<TEXTAREA COLS="50" ROWS="3" name="SESSION-description" WRAP=ON><% if(app!=null) { %><%=app.getDescription()%><% } %></TEXTAREA>
	</TD>
</TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Auto Deploy</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<SELECT name="SESSION-autodeploy"><OPTION value="no">no<option 
	<% if(app!=null && app.getAutoDeploy()) { %> SELECTED  <% } %> 
	value="yes">yes</SELECT>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Name of maintainer</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" name="SESSION-namemaintainer"
	<% if(app!=null) { %> VALUE="<%=app.getMaintainerName()%>" <% } %> >
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Email of maintainer</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" NAME="SESSION-emailmaintainer"
	<% if(app!=null) { %> VALUE="<%=app.getMaintainerEmail()%>" <% } %> >
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Company of maintainer</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" NAME="SESSION-companymaintainer"
	<% if(app!=null) { %> VALUE="<%=app.getMaintainerCompany()%>" <% } %> >
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Info of maintainer</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<TEXTAREA COLS="50" ROWS="3" name="SESSION-infomaintainer" WRAP=ON><% if(app!=null) { %><%=app.getMaintainerInfo()%><% } %></TEXTAREA>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B>Change information</B></FONT></A>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="SUBMIT" value="Process">
	</TD>
	</TR>
	</FORM>
</TABLE>

