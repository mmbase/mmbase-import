<mm:import externid="portal" id="po2" jspvar="portal" />
<mm:import externid="page" id="pa2" jspvar="page2" />
<mm:import externid="smaintainer" />
<mm:import externid="noffset" jspvar="noffset">0</mm:import>
<form action="advancedsearch.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />" method="POST">
<table cellspacing="0" cellpadding="0" class="list" width="97%">
<tr class="listsearch">
	<td width="50">
	<SELECT NAME="smaintainer">
	<mm:present referid="smaintainer">
	<mm:node number="$smaintainer">
                   <OPTION value="<mm:field name="number" />"><mm:field name="firstname" /> <mm:field name="lastname" />
	</mm:node>
	</mm:present>
	<mm:notpresent referid="smaintainer">
                   <OPTION value="-1">
		   <mm:import id="smaintainer" reset="true">-1</mm:import>
	</mm:notpresent>
	 <mm:list path="users,groups" constraints="groups.name='BugTrackerCommitors'">
                        <OPTION value="<mm:field name="users.number" />"><mm:field name="users.firstname" /> <mm:field name="users.lastname" />
        </mm:list>
	</SELECT>
	</td>
	<td width="50">
	<SELECT NAME="sstatus">
		<OPTION VALUE="">
		<OPTION VALUE="1">open
		<OPTION VALUE="2">accepted
		<OPTION VALUE="3">rejected
		<OPTION VALUE="4">pending
		<OPTION VALUE="5">integrated
		<OPTION VALUE="6">closed
	</SELECT>
	</td>
	<td width="50">
	<SELECT NAME="stype">
		<OPTION VALUE="">
		<OPTION VALUE="1">bug
		<OPTION VALUE="2">wish
		<OPTION VALUE="3">doc
		<OPTION VALUE="4">docwish
	</SELECT>
	</td>
	<td width="50">
	<SELECT NAME="spriority">
		<OPTION VALUE="">
		<OPTION VALUE="1">high
		<OPTION VALUE="2">medium
		<OPTION VALUE="3">low
	</SELECT>
	</td>
	<td width="50">
	<INPUT NAME="sversion" SIZE="3">
	</td>
	<td width="50">
	<SELECT NAME="sarea">
		<OPTION VALUE="">
		<mm:listnodes type="areas">
		<OPTION VALUE="<mm:field name="number" />"><mm:field name="substring(name,15,.)" />
		</mm:listnodes>
	</SELECT>
	</td>
	<td width="300">
	<INPUT NAME="sissue" SIZE="20">
	<a href="advancedsearch.jsp?portal=<%=portal%>&page=<%=page2%>">s</a>
	</td>
	<td>
	<INPUT TYPE="SUBMIT" VALUE="search">
	</td>
</tr>
</FORM>
<tr>
	<th width="50">
	Bug #
	</th>
	<th>
	Status
	</th>
	<th>
	Type
	</th>
	<th>
	Priority
	</th>
	<th>
	Version
	</th>
	<th>
	Area
	</th>
	<th>
	Issue
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<!-- the real searchpart -->

<mm:present referid="where" inverse="true">
</mm:present>
<mm:list path="pools,bugreports,areas" nodes="BugTracker.Start" orderby="bugreports.bugid" directions="down" constraints="">
	<mm:last>
		<mm:import id="total"><mm:index/></mm:import>
	</mm:last>
</mm:list>
<% String total="0"; %>
<mm:present referid="total">
  <mm:write referid="total" jspvar="tmp" vartype="integer">
	<% total = tmp.toString(); %>
  </mm:write>
</mm:present>

<%! String last="0"; %>
<mm:list path="pools,bugreports,rolerel,users" nodes="BugTracker.Start" orderby="bugreports.bugid" directions="down" constraints="rolerel.role='maintainer' and users.number=$smaintainer" max="100" offset="$noffset">
<tr>
		<td>
			#<mm:field name="bugreports.bugid" />
		</td>
		<td>
			 <mm:field name="bugreports.bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bugreports.btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bugreports.bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bugreports.version" />&nbsp;
		</td>
		<td>
			Unknown
		</td>
		<td>
			 <mm:field name="bugreports.issue" />&nbsp;
		</td>
		<td>
			<A HREF="/development/bugtracker/fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&bugreport=<mm:field name="bugreports.number" />"><IMG SRC="/development/bugtracker/images/arrow-right.gif" BORDER="0" ALIGN="right"></A>
		</td>
</tr>
<mm:last>
<mm:import id="last" reset="true" jspvar="last" ><mm:index offset="$noffset" /></mm:import>
</mm:last>
</mm:list>

<tr>
	<center>
	<% 
		int noffseti=1;
		int lasti=1;
		int totali=1;
		try {
			noffseti=Integer.parseInt(noffset);		
			lasti=Integer.parseInt(last);		
			totali=Integer.parseInt(total);		
		} catch(Exception e) {}
	%>
	<td colspan="3" class="listpaging">
			&nbsp;
			<mm:compare referid="noffset" value="0" inverse="true"><a href="index.jsp?portal=<%=portal%>&page=<%=page2%>&noffset=<%=(noffseti-15)%>"><img src="/development/bugtracker/images/arrow-left.gif" BORDER="0" align="right"></a></mm:compare>
	</td>
	<td colspan="3" class="listpaging" align="middle">
			<center>
			<% if (!total.equals("0")) { %>
			<%=(noffseti+1)%> to <%=(lasti+1)%> from <%=total%>
			<% } else { %>
				No bugs found in MMBase (ok not the one you are looking for)
			<% } %>
			</center>
	</td>
	<td colspan="2" class="listpaging">
			<% if (((lasti+1)!=totali) && !total.equals("0")) { %><a href="index.jsp?portal=<%=portal%>&page=<%=page2%>&noffset=<%=(noffseti+15)%>"><IMG SRC="/development/bugtracker/images/arrow-right.gif" BORDER="0" ALIGN="left"></a><% } %>
		&nbsp;
	</td>
</tr>

</table>
<!-- end of the searchpart -->
<center>
<table cellspacing="0" cellpadding="0" align="middle" width="80%">
<tr>

		<mm:present referid="user" inverse="true" >
			<td>
			 <center><font color="#000000">We have no idea who you are please login !<A HREF="/development/bugtracker/changeUser.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />"><IMG SRC="/development/bugtracker/images/arrow-right.gif" border="0" valign="middle"></A></font>
			</td>
		</mm:present>
		<mm:present referid="user">
			<td colspan="1">
			<br />
			<mm:node number="$user">
			<center> <font color="black">I am <mm:field name="firstname" /> <mm:field name="lastname" /> ( its not me , <A HREF="/development/bugtracker/changeUser.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />">change name</A> )<br /> i have a new bug and want to report it</font><A HREF="/development/bugtracker/newBug.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&user=<mm:write referid="user" />"><IMG SRC="/development/bugtracker/images/arrow-right.gif" BORDER="0" ></A>
			</td>
			</mm:node>
		</mm:present>
</tr>
</table>
</center>
</form>
