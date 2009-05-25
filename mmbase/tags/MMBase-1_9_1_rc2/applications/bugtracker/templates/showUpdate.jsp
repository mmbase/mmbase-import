<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
  <mm:cloud>
    <mm:import externid="updatereport" />
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

<mm:node number="$updatereport">
<mm:related path="rolerel,users" constraints="rolerel.role='maintainer'" max="1">
	<mm:import id="hasmaintainers">yes</mm:import>
</mm:related>

<table>
<tr>
  <th>Bug #</th><th>Status</th><th>Type</th><th>Priority</th><th>Version</th><th>Issue</th>
</tr>
<tr>
	<td>#<mm:field name="number" /></td>
   <td>
			 <mm:field name="bstatus"><%-- what a mess --%>
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</td>
		<td>
			<mm:field name="version" />&nbsp;
		</td>
		<td>
			<mm:field name="issue" />
		</td>
</tr>
<tr>
	<th>Description of the issue</th>
</tr>
<tr>
<td>
			<mm:field name="html(description)" />
			&nbsp;
		</td>
</tr>

<tr>
	<th>Rational from maintainer on state & priority</th>
</tr>
<tr>
	<td>
			<mm:field name="html(rationale)" />
			&nbsp;
		</td>
</tr>
</table>
</mm:node>
</mm:cloud>
