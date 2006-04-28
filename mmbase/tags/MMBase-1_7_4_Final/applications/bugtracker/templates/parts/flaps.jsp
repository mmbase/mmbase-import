<table width="98%" cellspacing="0" cellpadding="0" class="subnav">
	<tr>
	<td width="25">
	     <A HREF="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />"><IMG SRC="images/arrow-left.gif"></A>
	</td>
	<!-- overview flap -->
	<mm:compare referid="flap" value="overview" inverse="true">
		<td>
		<a href="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=overview&bugreport=<mm:write referid="bugreport" />">
		Overview
		</a>
		</td>
	</mm:compare>
	<mm:compare referid="flap" value="overview">
		<td class="selected">
		Overview
		</td>
	</mm:compare>



	<!-- history flap -->
	<mm:compare referid="flap" value="history" inverse="true">
		<td>
		<a href="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=history&bugreport=<mm:write referid="bugreport" />">
		History
		</a>
		</td>
	</mm:compare>
	<mm:compare referid="flap" value="history">
		<td class="selected">
		History
		</td>
	</mm:compare>


	<!-- change flap -->
	<mm:compare referid="flap" value="change" inverse="true">
		<td>
		<a href="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=change&bugreport=<mm:write referid="bugreport" />">
		Change
		</a>
		</td>
	</mm:compare>
	<mm:compare referid="flap" value="change">
		<td class="selected">
		Change
		</td>
	</mm:compare>


	<!-- mybug flap -->
	<mm:compare referid="flap" value="mybug" inverse="true">
		<td>
		<a href="fullview.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mybug&bugreport=<mm:write referid="bugreport" />">
		MyBug
		</a>
		</td>
	</mm:compare>
	<mm:compare referid="flap" value="mybug">
		<td class="selected">
		MyBug
		</td>
	</mm:compare>
	</tr>
</table>
