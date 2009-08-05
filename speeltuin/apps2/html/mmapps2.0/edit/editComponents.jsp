<%@page import="org.mmbase.module.core.*" %>
<%@page import="org.mmbase.application.*" %>
<%@page import="java.util.*" %>

<% String appname=request.getParameter("appname"); 
   ApplicationManager am=MMBase.getApplicationManager(); 
   Application ap=am.getApplication(appname);
%>
<HTML>
<TITLE>
</TITLE>
<BODY TEXT="#42BDAD" BGCOLOR="#00425a" LINK="#FFFFFF" ALINK="#FFFFFF" VLINK="#FFFFFF">
<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3><BR>
<A HREF="../applications.jsp">Applications</A> / <%=appname%> Components<BR><BR><BR>

<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="600" COLSPAN="2">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=3>
	<B>Application : <%=ap.getName()%></B>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#000000" COLSPAN="2">
	<FONT COLOR="#FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<%=ap.getMaintainerInfo()%><BR><BR>
	</FONT>
	</TD>
</TR>
<TR>
	<TD BGCOLOR="CCCCCC" WIDTH=300>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Description
	</TD>
	<TD BGCOLOR="CCCCCC" WIDTH=300>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Action
	</TD>
</TR>
<TR>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	Install all uninstalled components
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	<A HREF="../tasks/installApplication/start.shtml?<%=appname%>"><FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=3>install</a></TD>
</TR>
<TR>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	Delete this application (cannot be restored!)
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
        <A HREF="../actions/deleteApplication.shtml?<%=appname%>">delete</A>
</TR>
<TR>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	Edit Application Information
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
        <A HREF="editApplicationInfo.jsp?appname=<%=appname%>">Edit</A>
</TR>
<TR>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
	Save application (saves data, web pages, builders, everything).
	</TD>
	<TD BGCOLOR="#000000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>
        <A HREF="processSaveApplication.shtml?<%=appname%>">save</A>
</TR>
</TABLE>

<!-- ******************** show the components ******************** -->


<TABLE width=600 cellspacing=1 cellpadding=3 border=0>
<% for (Enumeration c = ap.getComponents().keys();c.hasMoreElements();) { 
   String cpname=(String)c.nextElement();
   Component cp=ap.getComponent(cpname);
%>
<TR>
	<TD>
	&nbsp;	
	</TD>
</TR>
<TR>
	<TD BGCOLOR="42BDAD" WIDTH="400" COLSPAN="5">
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Component : <%=cpname %></B>
	</TD>
</TR>

<!-- shows the components in the overview of a certain application -->
<% 
   Hashtable elv=cp.getElements();
%>

<!-- shows the header of the components listed in application.jsp -->
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
	<CENTER><B>Creation Date</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Description</B></CENTER>
	</TD>
	<TD BGCOLOR="#CCCCCC">
	<FONT FACE=helvetica,arial,geneva SIZE=3 COLOR="#000000">
	<CENTER><B>Delete</B></CENTER>
	</TD>
</TR>


<%
   for (Enumeration e = elv.keys();e.hasMoreElements();) { 
   String elname=(String)e.nextElement();
   ComponentElement el=cp.getElement(elname);
%>
<!-- shows information about an element. It's used in application.jsp -->
<PROCESSOR MMADMIN>
<TR>
	<LIST ELEMENT-$PARAM1-$PARAM2-$PARAM3 PROCESSOR="MMADMIN" ITEMS="1">
	<TD BGCOLOR="#00000">
	&nbsp;&nbsp;
	<A HREF="../elementinfo.shtml?<%=appname%>+<%=cpname%>+<%=elname%>">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2><B><%=elname%></B></FONT></A>
	&nbsp;
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
	<CENTER><%=el.getInstalledVersion()%></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
	<CENTER><%=el.getCreationDate()%></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
	<CENTER><%=el.getDescription()%></CENTER>
	</TD>
	<TD BGCOLOR="#00000">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>&nbsp;
	<CENTER><A HREF="deleteElement.shtml?<%=appname%>+<%=cpname%>+<%=elname%>">delete</a></CENTER>
	</TD>
</LIST>
</TR>
<% } %>
<TR>
	<TD BGCOLOR="#00000" colspan="5">
	&nbsp;
	<A HREF="edit<%=cpname%>Element.shtml?<%=appname%>+<%=cpname%>">
	<FONT COLOR="FFFFFF" FACE=helvetica,arial,geneva SIZE=2>Add <%=cpname%> Element</B></FONT></A>
	</TD>
</TR>
<% } %>
</TABLE>
<BR><BR>
</BODY>
</HTML>
