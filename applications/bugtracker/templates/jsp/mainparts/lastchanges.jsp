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
	<B>Bug #</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>state</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>time</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=2>
	<B>issue</B>
	</TD>
	<TD BGCOLOR="#42BDAD" >
	<FONT COLOR="#000000" FACE=helvetica,arial,geneva SIZE=1>
	&nbsp;
	</TD>
</TR>
<!-- the real searchpart -->

<mm:list path="bugreports" orderby="bugreports.time" directions="down">
	<mm:last>
    <!--  <mm:index id="total" />  -->
	</mm:last>
</mm:list>


<% String total="0"; %>
<mm:present referid="total">
  <mm:write referid="total" jspvar="tmp2" vartype="string">
	<% total=tmp2; %>
  </mm:write>
</mm:present>



<mm:listnodes type="bugreports" orderby="time" directions="down" max="15" offset="$offset">
<TR>
		<TD WIDTH="30"></TD>
		<TD BGCOLOR="#00425A">
			#<mm:field name="bugid" />
		</TD>
		<TD BGCOLOR="#00425A">
			 <mm:field name="bstatus">
				<mm:compare value="1">Open</mm:compare>
				<mm:compare value="2">Accepted</mm:compare>
				<mm:compare value="3">Rejected</mm:compare>
				<mm:compare value="4">Pending</mm:compare>
				<mm:compare value="5">Integrated</mm:compare>
				<mm:compare value="6">Closed</mm:compare>
			 </mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			<mm:field name="time">
				<mm:time format="HH:mm:ss, EE d MM yyyy" />
			</mm:field>
		</TD>
		<TD BGCOLOR="#00425A">
			<mm:field name="issue" />
		</TD>
		<TD BGCOLOR="#44BDAD">
			<A HREF="fullview.jsp?bugreport=<mm:field name="number" />"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></A>
		</TD>
</TR>
<mm:last>
 <!-- <mm:index id="last" offset="$offset" /> --> 
</mm:last>
</mm:listnodes>
<% int last2 = 0; %>
<mm:present referid="last">
  <mm:write referid="last" jspvar="t" vartype="integer">
    <% last2 = t.intValue(); %>
  </mm:write>
</mm:present>


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
			lasti= last2;		
			totali=Integer.parseInt(total);		
		} catch(Exception e) {}
	%>
	<TABLE  cellspacing=0 cellpadding=3 border=0>
	<TD BGCOLOR="#42BDAD">
			<mm:compare referid="offset" value="0" inverse="true"><a href="index.jsp?flap=lastchanges&offset=<%=(offseti-15)%>&where=<%=where%>"><IMG SRC="images/arrow2.gif" BORDER="0" ALIGN="left"></a></mm:compare>
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
			<% if (((lasti+1)!=totali) && !total.equals("0")) { %><a href="index.jsp?flap=lastchanges&offset=<%=(offseti+15)%>&where=<%=where%>"><IMG SRC="images/arrow.gif" BORDER="0" ALIGN="left"></a><% } %>
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
