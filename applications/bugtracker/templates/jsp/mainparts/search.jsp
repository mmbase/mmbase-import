<form action="index.jsp" method="POST">
<TABLE  cellspacing=1 cellpadding=3 border=0>
<TR>
	<TD>
	<BR>
	</TD>
</TR>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT NAME="sbugid" SIZE="4">
	</TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<SELECT NAME="sstatus">
		<OPTION VALUE="">
		<OPTION VALUE="1">open
		<OPTION VALUE="2">accepted
		<OPTION VALUE="3">rejected
		<OPTION VALUE="4">pending
		<OPTION VALUE="5">integrated
		<OPTION VALUE="6">closed
	</SELECT>
	</TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<SELECT NAME="stype">
		<OPTION VALUE="">
		<OPTION VALUE="1">bug
		<OPTION VALUE="2">wish
		<OPTION VALUE="3">doc
		<OPTION VALUE="4">docwish
	</SELECT>
	</TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<SELECT NAME="spriority">
		<OPTION VALUE="">
		<OPTION VALUE="1">high
		<OPTION VALUE="2">medium
		<OPTION VALUE="3">low
	</SELECT>
	</TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT NAME="sversion" SIZE="3">
	</TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<SELECT NAME="sarea">
		<OPTION VALUE="">
		<mm:listnodes type="areas">
		<OPTION VALUE="<mm:field name="number" />"><mm:field name="substring(name,15,.)" />
		</mm:listnodes>
	</SELECT>
	</TD>
	<TD BGCOLOR="#42BDAD" width="300">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<INPUT NAME="sissue" SIZE="20">
	</TD>
	<TD BGCOLOR="#42BDAD">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=1>
	<INPUT TYPE="SUBMIT" VALUE="search">
	</TD>
</TR>
</FORM>
<TR>
	<TD>
	</TD>
</IR>
<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#42BDAD" width="50">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Bug #</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Status</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Type</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Priority</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Version</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Area</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>Issue</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=1>
	&nbsp;
	</TD>
</TR>
<!-- the real searchpart -->

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
<mm:list path="pools,bugreports,areas" nodes="BugTracker.Start" orderby="bugreports.number" directions="down" constraints="<%=where%>" max="15" offset="$offset">
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A">
			#<mm:field name="bugreports.bugid" />
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bugreports.bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bugreports.btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bugreports.bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bugreports.version" />&nbsp;
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="areas.name" />&nbsp;
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bugreports.issue" />&nbsp;
		</TD>
		<TD BGCOLOR="#44BDAD">
			<A HREF="fullview.jsp?bugreport=<mm:field name="bugreports.number" />"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></A>
		</TD>
</TR>
<mm:last>
<mm:import id="last" reset="true" jspvar="last" ><mm:index offset="$offset" /></mm:import>
</mm:last>
</mm:list>

<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD COLSPAN="7">
	<CENTER>
	<% 
		int offseti=1;
		int lasti=1;
		int totali=1;
		try {
			offseti=Integer.parseInt(offset);		
			lasti=Integer.parseInt(last);		
			totali=Integer.parseInt(total);		
		} catch(Exception e) {}
	%>
	<TABLE  cellspacing=0 cellpadding=3 border=0>
	<TD BGCOLOR="#42BDAD">
			<mm:compare referid="offset" value="0" inverse="true"><a href="index.jsp?offset=<%=(offseti-15)%>&where=<%=where%>"><IMG SRC="images/arrow2.gif" BORDER="0" ALIGN="left"></a></mm:compare>
	</TD>
	<TD BGCOLOR="#42BDAD">
		<FONT COLOR="#000000">
			<% if (!total.equals("0")) { %>
			<%=(offseti+1)%> to <%=(lasti+1)%> from <%=total%>
			<% } else { %>
				No bugs found in MMBase (ok not the one you are looking for)
			<% } %>
		
		</FONT>
	</TD>
	<TD BGCOLOR="#42BDAD">
			<% if (((lasti+1)!=totali) && !total.equals("0")) { %><a href="index.jsp?offset=<%=(offseti+15)%>&where=<%=where%>"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></a><% } %>
	</TD>
	</TABLE>
	</CENTER>
	</TD>
</TR>




<!-- end of the searchpart -->
<TR>
	<TD>
		&nbsp;
	</TD>
</TR>

<TR>
	<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
	<TD BGCOLOR="#42BDAD" COLSPAN="7">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>New bug report</B>
	</TD>
	<TD BGCOLOR="#42BDAD">
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>&nbsp;</B>
	</TD>
</TR>
<TR>

		<TD WIDTH="30"></TD>
		<mm:present referid="user" inverse="true" >
			<TD BGCOLOR="#00425A" COLSPAN="7">
			 <center>We have no idea who you are please login !
			</TD>
			<TD BGCOLOR="#44BDAD" WIDTH="14">
				<A HREF="changeUser.jsp"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></A>
			</TD>
		</mm:present>
		<mm:present referid="user">
			<TD BGCOLOR="#00425A" cOLSPAN="7">
			<mm:node number="$user">
			<CENTER> I am <mm:field name="firstname" /> <mm:field name="lastname" /> ( its not me , <A HREF="changeUser.jsp">change name</A> ) i have a new bug and want to report it
			</TD>
			<TD BGCOLOR="#44BDAD" WIDTH="14">
				<A HREF="newBug.jsp?user=<mm:write referid="user" />"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></A>
			</TD>
			</mm:node>
		</mm:present>
</TR>
</TABLE>
</form>
