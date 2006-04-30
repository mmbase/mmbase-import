<% 
	int stats_rtotal=0;
	int stats_ropen=0;
	int stats_rclosed=0;
	int stats_wtotal=0;
	int stats_wopen=0;
	int stats_wclosed=0;
%>

<mm:listnodes path="pools,bugreports" element="bugreports">
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
</mm:listnodes>

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
  <th width="180">Type</th><th width="180">State</th><th>Comment</th>
</tr>
<tr>
  <td width="180">Total Bugs: <%=stats_rtotal%></td>
  <td width="180">Open Bugs : <%=stats_ropen%></td>
  <td>MMBase is <%=stats_fun1 %> % broken</td>
</tr>

<tr>
  <td width="180">&nbsp;</td>
  <td width="180">Closed Bugs : <%=stats_rclosed%></td>
  <td>MMBase is <%=stats_fun2 %> % working </td>
</tr>

<tr>
  <td width="180">Total Wishes: <%=stats_wtotal%></td>
  <td width="180">Open Wishes : <%=stats_wopen%></td>
  <td>Submitters want <%=stats_fun3 %> % new features</td>
</tr>

<tr>
  <td width="180">&nbsp;</td>
  <td width="180">Closed Wishes : <%=stats_wclosed%></td>
  <td>Submitters are <%=stats_fun4 %> % Happy</td>
</tr>
</table>

<table width="98%" cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;">
<tr>
  <th width="180">Developer</th>
  <th width="180">Commited</th>
  <th>updated</th>
  <th>Maintainer</th>
  <th>Interested</th>
</tr>

<mm:listnodes type="users" orderby="lastname">
  <tr <mm:even>class="even"</mm:even>>
		<td width="180">
			 <mm:field name="firstname" />
			 <mm:field name="lastname" />
		</td>
		<td width="180">
      <mm:relatedcontainer path="rolerel,bugreports">
        <mm:constraint field="rolerel.role" value="submitter" />
        <mm:size />
      </mm:relatedcontainer>
		</td>
		<td width="180">
      <mm:relatedcontainer path="rolerel,bugreports">
        <mm:constraint field="rolerel.role" value="updater" />
        <mm:size />
      </mm:relatedcontainer>
		</td>
		<td width="180">
      <mm:relatedcontainer path="rolerel,bugreports">
        <mm:constraint field="rolerel.role" value="maintainer" />
        <mm:size />
      </mm:relatedcontainer>
		</td>
		<td>
      <mm:relatedcontainer path="rolerel,bugreports">
        <mm:constraint field="rolerel.role" value="interested" />
        <mm:size />
      </mm:relatedcontainer>
		</td>
</tr>
</mm:listnodes>
</table>
<p />
