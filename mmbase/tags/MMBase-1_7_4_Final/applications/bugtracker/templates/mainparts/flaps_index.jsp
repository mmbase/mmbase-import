<table cellpadding="0" cellspacing="0" width="100%" class="subnav">
<tr>
	<!-- overview flap -->
	<mm:compare referid="flap" value="search" inverse="true">
		<td>
		<a href="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=search">Search</a>
		</td>
	</mm:compare>
	<mm:compare referid="flap" value="search">
		<td class="selected">Search</td>
	</mm:compare>

	<!-- comments flap -->
	<mm:compare referid="flap" value="lastchanges" inverse="true">
		<td><a href="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=lastchanges">Last changed</a></td>
	</mm:compare>
	<mm:compare referid="flap" value="lastchanges">
		<td class="selected">Last changed</td>
	</mm:compare>


	<!-- history flap -->
	<mm:compare referid="flap" value="stats" inverse="true">
		<td><a href="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=stats">Statistics</a></td>
	</mm:compare>
	<mm:compare referid="flap" value="stats">
		<td class="selected">Statistics</td>
	</mm:compare>

	<!-- change flap -->
	<mm:compare referid="flap" value="mysettings" inverse="true">
		<td><a href="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mysettings">MySettings</a></td>
	</mm:compare>
	<mm:compare referid="flap" value="mysettings">
		<td class="selected">MySettings</td>
	</mm:compare>

	<!-- mybug flap -->
	<mm:compare referid="flap" value="mybug" inverse="true">
		<td><a href="index.jsp?portal=<mm:write referid="portal" />&page=<mm:write referid="page" />&flap=mybug">MyBug</a></td>
	</mm:compare>
	<mm:compare referid="flap" value="mybug">
		<td class="selected">MyBug</td>
        </mm:compare>
  </tr>
</table>

<table cellpadding="0" cellspacing="0" width="100%" class="layout">
<tr>
<td>
&nbsp;
</td>
</tr>
</table>
