<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% ApplicationManager am = MMBase.getApplicationManager(); 
   Provider provider = am.getProvider();
%>


<HTML>
<HEAD>
</HEAD>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3><BR>
<A HREF="../applications.jsp">Applications</A> / manage local provider<BR><BR><BR>


<!-- ********************* shows the general Provider settings *************** -->
<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="2">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Provider information</B>
	</TD>
</TR>

<FORM METHOD="POST" ACTION="setProviderInfo.jsp">
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Name</B></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><INPUT TYPE="TEXT" NAME="name" VALUE="<%=provider.getName()%>"></CENTER>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Description</B></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><TEXTAREA COLS="50" ROWS="3" name="description"><%=provider.getDescription()%></TEXTAREA></CENTER>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Process</B></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<CENTER><INPUT TYPE="SUBMIT" VALUE="gooo"></CENTER>
	</TD>
</TR>
</FORM>
</TABLE>
<BR><BR>

<!-- ********************** Which users can access which applications ************ -->
<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="3">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>User access to applications</B>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<B>Application name</B>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<B>User name</B>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<B>Action</B>
	</TD>
</TR>
<% for (Enumeration e = provider.getProvidedApplications().keys();e.hasMoreElements();) {
	String application = (String)e.nextElement();
	Vector users = (Vector)provider.getUsersOfApplication(application);

	for(Enumeration u = users.elements();u.hasMoreElements();) {
		String user = (String)u.nextElement();
	%>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<%=application%>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<%=user%>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<A HREF="deleteAccesToApplication.jsp?application=<%=application%>&user=<%=user%>">remove access</A>
	</TD>
</TR>

<% } %>
<% } %>
<FORM ACTION="addUserToApplication.jsp" METHOD="POST">
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
  	<SELECT NAME="application">
	<% for (Enumeration e = am.getApplications().keys();e.hasMoreElements();) {
		String application = (String)e.nextElement(); %>
        <OPTION VALUE="<%=application%>"><%=application%>
	<% } %>
        </SELECT>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
  	<SELECT NAME="user">
	<% for (Enumeration e = provider.getUserInfo().keys();e.hasMoreElements();) {
		String user = (String)e.nextElement(); %>
        <OPTION VALUE="<%=user%>"><%=user%>
	<% } %>
	</SELECT>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="SUBMIT" VALUE="Add Access">
	</TD>
</TR>
</FORM>
</TABLE>
<BR><BR>

<!-- ******************* Manage users with passwords ********************** -->
<TABLE width=600 cellspacing=1 cellpadding=3 border=0>

<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN=3">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>User information</B>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<B>UserId</B>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<B>Password</B>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=2 COLOR="#000000">
	<b>Action</B>
	</TD>
</TR>
<% for (Enumeration e = provider.getUserInfo().keys();e.hasMoreElements();) {
	String userid = (String)e.nextElement(); %>
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<%=userid%>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<%=provider.getPassword(userid)%>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<A HREF="deleteUser.jsp?user=<%=userid%>">delete user</A>
	</TD>
</TR>
<% } %>
<FORM ACTION="addUser.jsp" METHOD="POST">
<TR>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" NAME="user">
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="TEXT" NAME="password">
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT TYPE="SUBMIT" VALUE="gogogo">
	</TD>
</TR>
</FORM>
</TABLE>

