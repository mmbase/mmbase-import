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
<table border="0" width="98%" cellpadding="0" cellspacing="0" class="list">

<tr>
		<th WIDTH="180">
			 Type
		</th>
		<th WIDTH="180">
			 State
		</th>
		<th>
			 Comment
		</th>
</tr>
<tr>
		<td width="180">
			 Total Bugs: <%=stats_rtotal%>
		</td>
		<td width="180">
			Open Bugs : <%=stats_ropen%>
		</td>
		<td>
			MMBase is <%=stats_fun1 %> % broken
		</td>
</tr>

<tr>
		<td WIDTH="180">
			&nbsp;
		</td>
		<td width="180">
			Closed Bugs : <%=stats_rclosed%>
		</td>
		<td>
			MMBase is <%=stats_fun2 %> % working
		</td>
</tr>

<tr>
		<td WIDTH="180">
			 Total Wishes: <%=stats_wtotal%>
		</td>
		<td width="180">
			Open Wishes : <%=stats_wopen%>
		</td>
		<td>
			Submitters want <%=stats_fun3 %> % new features
		</td>
</tr>

<tr>
		<td width="180">
			&nbsp;
		</td>
		<td width="180">
			Closed Wishes : <%=stats_wclosed%>
		</td>
		<td>
			Submitters are <%=stats_fun4 %> % Happy
		</td>
</tr>
</table>

<table width="98%" cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;">
<tr>
		<th width="180">
			 Developer
		</th>
		<th width="180">
			 <FONT COLOR="#000000">Commited</font>
		</th>
		<th>
			 updated
		</th>
		<th>
			 Maintainer
		</th>
		<th>
			 Interested
		</th>
</tr>
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
<tr>
		<td width="180">
			 <mm:field name="firstname" />
			 <mm:field name="lastname" />
		</td>
		<td width="180">
			 <%=stats_sub %>
		</td>
		<td width="180">
			 <%=stats_updater %>
		</td>
		<td width="180">
			 <%=stats_maintainer %>
		</td>
		<td>
			 <%=stats_interested %>
		</td>
</tr>
</mm:listnodes>
</table>
<p />
