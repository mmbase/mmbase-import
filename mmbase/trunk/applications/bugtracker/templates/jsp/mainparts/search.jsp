<table>
<tr>
	<td width="50"><img src="images/trans.gif" /></td>
	<td>
	<input name="sbugid" size="4" />
	</td>
	<td width="50">
	<select name="sstatus">
		<option value="" />
		<option value="1">open</option>
		<option value="2">accepted</option>
		<option value="3">rejected</option>
		<option value="4">pending</option>
		<option value="5">integrated</option>
		<option value="6">closed</option>
	</select>
	</td>
	<td bgcolor="#42BDAD" width="50">
	<select name="stype">
		<option value="" />
		<option value="1">bug</option>
		<option value="2">wish</option>
		<option value="3">doc</option>
		<option value="4">docwish</option>
	</select>
	</td>
	<td>
	<select name="spriority">
		<option value="" />
		<option value="1">high</option>
		<option value="2">medium</option>
		<option value="3">low</option>
	</select>
	</td>
	<td>
	<input name="sversion" size="3">
	</td>
	<td>
	<select name="sarea">
		<option value="" />
		<mm:listnodes type="areas">
   		<option value="<mm:field name="number" />"><mm:field name="substring(name,15,.)" /></option>
		</mm:listnodes>
	</select>
	</td>
	<td>
	<input name="sissue" size="20" />
	</td>
	<td>
	<input type="submit" value="search" />
	</td>
</tr>
</form>
<tr>
	<td><img src="images/trans.gif" width="50" height="1"></td>
	<td>
	<b>bug #</b>
	</td>
	<td>
	<b>Status</b>
	</td>
	<td>
	<b>Type</b>
	</td>
	<td>
	<b>Priority</b>
	</td>
	<td>
	<b>Version</b>
	</td>
	<td>
	<b>Area</b>
	</td>
	<td>
	<b>Issue</b>
	</td>
	<td>
	&nbsp;
	</td>
</tr>

<mm:present referid="where" inverse="true">
<% 	where="";
	if (sissue!=null && !sissue.equals(""))  where+="issue like '%"+sissue+"%'";
	if (sstatus!=null && !sstatus.equals("")) { if (!where.equals("")) where+=" and ";where+="bstatus="+sstatus; }
	if (stype!=null && !stype.equals("")) { if (!where.equals("")) where+=" and ";where+="btype="+stype; }
	if (sversion!=null && !sversion.equals("")) { if (!where.equals("")) where+=" and ";where+="version like '%"+sversion+"%'"; }
	if (sbugid!=null && !sbugid.equals("")) { if (!where.equals("")) where+=" and ";where+="bugreports.bugid="+sbugid; }
	if (sarea!=null && !sarea.equals("")) { if (!where.equals("")) where+=" and ";where+="areas.number="+sarea; }
	if (spriority!=null && !spriority.equals("")) { if (!where.equals("")) where+=" and ";where+="bugreports.bpriority="+spriority; }
%>
</mm:present>

<mm:list path="pools,bugreports,areas" nodes="BugTracker.Start" orderby="bugreports.number" directions="down" constraints="<%=where%>">
	<mm:last>
     <mm:index id="total" />
	</mm:last>
</mm:list>
<% String total="0"; %>
<mm:present referid="total">
  <mm:write referid="total" jspvar="tmp" vartype="integer">
	<% total = tmp.toString(); %>
  </mm:write>
</mm:present>

<mm:list path="pools,bugreports,areas" nodes="BugTracker.Start" orderby="bugreports.number" directions="down" constraints="<%=where%>" max="15" offset="$offset">
<tr>
		<td width="30" />
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
			 <mm:field name="areas.name" />&nbsp;
		</td>
		<td>
			 <mm:field name="bugreports.issue" />&nbsp;
		</td>
		<td>
			<a href="<mm:url page="fullview.jsp"><mm:param name="bugreport"><mm:field name="bugreports.number" /></mm:param></mm:url>"><img src="images/arrow.gif" /></a>
		</td>
</tr>
<mm:last>
  <mm:index id="last" offset="$offset" />
</mm:last>
</mm:list>

<% int last = 0; %>
<mm:present referid="last">
  <mm:write referid="last" jspvar="l" vartype="integer">
     <% last = l.intValue(); %>
  </mm:write>
</mm:present>

<tr>
	<td width="50"><img src="images/trans.gif" width="50" height="1"></td>
	<td colspan="7">
	<center>
	<% 
		int offseti=1;
		int lasti=1;
		int totali=1;
		try {
			offseti=Integer.parseInt(offset);		
			lasti= last ;		
			totali=Integer.parseInt(total);		
		} catch(Exception e) {}
	%>
	<table>
	<td>
			<mm:compare referid="offset" value="0" inverse="true"><a href="index.jsp?offset=<%=(offseti-15)%>&where=<%=where%>"><IMG SRC="images/arrow2.gif" BORDER="0" ALIGN="left" /></a></mm:compare>
	</td>
	<td>
			<% if (!total.equals("0")) { %>
			<%=(offseti+1)%> to <%=(lasti+1)%> from <%=total%>
			<% } else { %>
				No bugs found in MMBase (ok not the one you are looking for)
			<% } %>
		
	</td>
	<td>
			<% if (((lasti+1)!=totali) && !total.equals("0")) { %><a href="index.jsp?offset=<%=(offseti+15)%>&where=<%=where%>"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></a><% } %>
	</td>
	</table>
	</center>
	</td>
</tr>

<tr>
	<td><img src="images/trans.gif" width="50" height="1" /></td><%-- arch --%>
	<td  colspan="7">
	<b>New bug report</b>
	</td>
</tr>
<tr>

		<td width="30" />
		<mm:present referid="user" inverse="true" >
			<td colspan="7">
			 <center>We have no idea who you are please login !
			</td>
			<td width="14">
				<a href="<mm:url page="changeUser.jsp" />"><img src="images/arrow.gif" /></a>
			</td>
		</mm:present>
		<mm:present referid="user">
			<td  colspan="7">
			<mm:node number="$user">
			<center> I am <mm:field name="firstname" /> <mm:field name="lastname" /> ( its not me , <a href="<mm:url page="changeUser.jsp" />">change name</a> ) I have a new bug and want to report it
			</td>
			<td width="14">
				<a href="newBug.jsp?user=<mm:write referid="user" />"><img src="images/arrow.gif" /></a>
			</td>
			</mm:node>
		</mm:present>
</tr>
</table>
