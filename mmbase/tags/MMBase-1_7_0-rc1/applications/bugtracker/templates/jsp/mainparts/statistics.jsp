<table border="0" width="100%">
<% 
	int stats_rtotal=0;
	int stats_ropen=0;
	int stats_rclosed=0;
	int stats_wtotal=0;
	int stats_wopen=0;
	int stats_wclosed=0;
%>

<mm:list path="pools,bugreports">
	<mm:node element="bugreports">
	<mm:field name="btype">
		<mm:compare value="1">
		<% stats_rtotal++; %>
		<mm:field name="bstatus">
			<mm:compare value="1"><% stats_ropen++; %></mm:compare>
			<mm:compare value="2"><% stats_ropen++; %></mm:compare>
			<mm:compare value="4"><% stats_ropen++; %></mm:compare>
			<mm:compare value="3"><% stats_rclosed++; %></mm:compare>
			<mm:compare value="5"><% stats_rclosed++; %></mm:compare>
			<mm:compare value="6"><% stats_rclosed++; %></mm:compare>
		</mm:field>
		</mm:compare>
		<mm:compare value="2">
		<% stats_wtotal++; %>
		<mm:field name="bstatus">
			<mm:compare value="1"><% stats_wopen++; %></mm:compare>
			<mm:compare value="2"><% stats_wopen++; %></mm:compare>
			<mm:compare value="4"><% stats_wopen++; %></mm:compare>
			<mm:compare value="3"><% stats_wclosed++; %></mm:compare>
			<mm:compare value="5"><% stats_wclosed++; %></mm:compare>
			<mm:compare value="6"><% stats_wclosed++; %></mm:compare>
		</mm:field>
		</mm:compare>
	</mm:field>
	</mm:node>
</mm:list>

<% 
	float stats_fun1=100;
	try {
		stats_fun1=100-(100*(((float)stats_rtotal-stats_ropen)/stats_rtotal)); 
	} catch(Exception e) {}

	float stats_fun2=100;
	try {
		stats_fun2=100-(100*(((float)stats_rtotal-stats_rclosed)/stats_rtotal)); 
	} catch(Exception e) {}
	float stats_fun3=100;
	try {
		stats_fun3=100-(100*(((float)stats_wtotal-stats_wopen)/stats_wtotal)); 
	} catch(Exception e) {}
	float stats_fun4=100;
	try {
		stats_fun4=100-(100*(((float)stats_wtotal-stats_wclosed)/stats_wtotal)); 
	} catch(Exception e) {}
%>
<TR>
	<TD>
	<br />
	</TD>
</TR>
</TABLE>
<table border="0" width="100%" cellpadding="3">

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <FONT COLOR="#000000">Type</FONT>
		</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <FONT COLOR="#000000">State</FONT>
		</TD>
		<TD BGCOLOR="#42BDAD">
			 <FONT COLOR="#000000">Comment</FONT>
		</TD>
</TR>
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" width="180">
			 Total Bugs: <font color="#ffffff"><%=stats_rtotal%></font>
		</TD>
		<TD BGCOLOR="#00425A" width="180">
			Open Bugs : <font color="#ffffff"><%=stats_ropen%></font>
		</TD>
		<TD BGCOLOR="#00425A"">
			MMBase is <font color="#ffffff"> <%=stats_fun1 %> % </font> broken
		</TD>
</TR>

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" width="180">
			Closed Bugs : <font color="#ffffff"><%=stats_rclosed%></font>
		</TD>
		<TD BGCOLOR="#00425A"">
			MMBase is <font color="#ffffff"> <%=stats_fun2 %> % </font> working
		</TD>
</TR>

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			 Total Wishes: <font color="#ffffff"><%=stats_wtotal%></font>
		</TD>
		<TD BGCOLOR="#00425A" width="180">
			Open Wishes : <font color="#ffffff"><%=stats_wopen%></font>
		</TD>
		<TD BGCOLOR="#00425A"">
			Submitters want <font color="#ffffff"> <%=stats_fun3 %> % </font> new features
		</TD>
</TR>

<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			&nbsp;
		</TD>
		<TD BGCOLOR="#00425A" width="180">
			Closed Wishes : <font color="#ffffff"><%=stats_wclosed%></font>
		</TD>
		<TD BGCOLOR="#00425A"">
			Submitters are <font color="#ffffff"> <%=stats_fun4 %> % </font> Happy
		</TD>
</TR>
<TR>
	<TD>
		<br />
	</TD>
</TR>
</table>

<table border="0" width="100%" cellpadding="3">
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <FONT COLOR="#000000">Developer</font>
		</TD>
		<TD BGCOLOR="#42BDAD" WIDTH="180">
			 <FONT COLOR="#000000">Commited</font>
		</TD>
		<TD BGCOLOR="#42BDAD">
			 <FONT COLOR="#000000">Updated</FONT>
		</TD>
		<TD BGCOLOR="#42BDAD">
			 <FONT COLOR="#000000">Maintainer</FONT>
		</TD>
		<TD BGCOLOR="#42BDAD">
			 <FONT COLOR="#000000">Interested</FONT>
		</TD>
</TR>
<% int stats_sub=0; 
   int stats_updater=0;
   int stats_maintainer=0;
   int stats_interested=0;
%>

<mm:listnodes type="users" orderby="lastname">
	<% stats_sub=0; 
	   stats_updater=0;
	   stats_maintainer=0;
	   stats_interested=0;
	%>
	<mm:related path="rolerel,bugreports">
		<mm:field name="rolerel.role">
			<mm:compare value="submitter">
				<% stats_sub++; %>
			</mm:compare>
			<mm:compare value="updater">
				<% stats_updater++; %>
			</mm:compare>
			<mm:compare value="maintainer">
				<% stats_maintainer++; %>
			</mm:compare>
			<mm:compare value="interested">
				<% stats_interested++; %>
			</mm:compare>
		</mm:field>
	</mm:related>
<TR>
		<TD WIDTH="50">&nbsp;</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			 <mm:field name="firstname" />
			 <mm:field name="lastname" />
			</font>
		</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			 <%=stats_sub %>
			 </font>
		</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			 <%=stats_updater %>
			 </font>
		</TD>
		<TD BGCOLOR="#00425A" WIDTH="180">
			 <%=stats_maintainer %>
			 </font>
		</TD>
		<TD BGCOLOR="#00425A">
			 <%=stats_interested %>
			 </font>
		</TD>
</TR>
</mm:listnodes>

</table>
