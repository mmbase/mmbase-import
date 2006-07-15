<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
		<th>
			  Bug
		</th>
		<td>
			  #<mm:field name="bugid" />
		</td>
		<th>
			Issue	
		</th>
		<td>
			<mm:field name="issue" />
		</td>
</tr>

<tr>
		<th>
			Status	
		</th>
		<td>
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</td>
		<th>
			Area
		</th>
		<td>
			<mm:relatednodes type="areas">
			Area : <mm:field name="name" />
			</mm:relatednodes>
		</td>
</tr>


<tr>
		<th>
			Type	
		</th>
		<td>
			 <mm:field name="btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</td>
		<th>
			Priority	
		</th>
		<td>
			 <mm:field name="bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</td>
</tr>

<tr>
		<th>
			Version	
		</th>
		<td>
			 <mm:field name="version" />
		</td>
		<th>
			Expected fixed in		
		</th>
		<td>
			 <mm:field name="efixedin">
				<mm:compare value="" inverse="true">
			 		<mm:field name="efixedin" />
				</mm:compare>
				<mm:compare value="">
					Unknown
				</mm:compare>
			 </mm:field>
		</td>
</tr>


<tr>
		<th>
			Submitter	
		</th>
		<td>
				<mm:related path="rolerel,users" max="1" constraints="rolerel.role='submitter'">
				<a href="showUser.jsp?showuser=<mm:field name="users.number" />"><mm:field name="users.firstname" /> <mm:field name="users.lastname" /></a>
				</mm:related>
		</td>
		<th>
			Submitted	
		</th>
		<td>
			<mm:relatednodes type="bugreportupdates" orderby="time" max="1">
				<mm:first>
					<mm:import id="found" />
				</mm:first>
				<mm:field name="time">
					<mm:time format="HH:mm:ss, EE d MM yyyy" />
				</mm:field>
				<mm:last>
				</mm:last>
			</mm:relatednodes>
			 <mm:present referid="found">Last update : </mm:present><mm:present referid="found" inverse="true">Submitted :</mm:present> 
		<mm:field name="time">
			<mm:time format="HH:mm:ss, EE d MM yyyy" />
		</mm:field>
		</td>
</tr>


<tr>
		<th>
			Maintainer	
		</th>
		<td>
				<mm:related path="rolerel,users" constraints="rolerel.role='maintainer'">
				<mm:first>
					<mm:import id="mfound">yes</mm:import>
				</mm:first>
<a href="showUser.jsp?showuser=<mm:field name="users.number" />"> <mm:field name="users.firstname" /> <mm:field name="users.lastname" /></a><br />
				</mm:related>
				<mm:present referid="mfound" inverse="true">
				none assigned
				</mm:present>
		</td>
		<th>
			Confirmed fixed in	
		</th>
		<td>
			 <mm:field name="fixedin">
				<mm:compare value="" inverse="true">
			 		<mm:field name="fixedin" />
				</mm:compare>
				<mm:compare value="">
					Unknown
				</mm:compare>
			 </mm:field>
		</td>
</tr>

<tr>
		<th>
			 Longer bug description	
		</th>
		<td colspan="3">
				<mm:field name="html(description)" />
		</td>
</tr>
</table>
