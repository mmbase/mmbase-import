<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>>  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>
<mm:cloud>
<mm:import externid="bugreport" />
<form action="executes/addSubmitter.jsp?bugreport=<mm:write referid="bugreport" />" METHOD="POST">
<TABLE width="90%" cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B> current Submitter</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>New Submitter</B>
	</TD>
	<TD BGCOLOR="42BDAD" COLSPAN=1>
	<FONT COLOR="000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Action</B>
	</TD>
</TR>

<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<mm:list path="bugreports,rolerel,users" nodes="$bugreport" constraints="rolerel.role='submitter'">
			<mm:field name="users.firstname" /> <mm:field name="users.lastname" />
			<BR>
			</mm:list>
				&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
			<SELECT NAME="submitter">

				<mm:list path="users,groups" nodes="-1" constraints="groups.name='BugTrackerCommitors'">
				<OPTION VALUE="<mm:field name="users.number" />"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
				</mm:list>
				&nbsp;
			</SELECT>
		</TD>
		<TD BGCOLOR="#00425A" COLSPAN="1">
		<INPUT TYPE="SUBMIT" VALUE="SAVE">
		</TD>
</TR>
</TABLE>
</FORM>
</mm:cloud>
</BODY>
</HTML>
