<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
 <TR>
	<!-- bundles -->
	<mm:compare referid="main" value="projects" inverse="true">
		<mm:compare referid="sub" value="none">
		<td align="middle">
		<a href="index.jsp?main=projects">
		Projects
		</a>
		</td>
		</mm:compare>
	</mm:compare>
	<mm:compare referid="main" value="projects">
		<th>
		<a href="index.jsp?main=projects">
		Projects
		</a>
		</th>
	</mm:compare>

	<!-- settings -->
	<mm:compare referid="main" value="settings" inverse="true">
		<td align="middle">
		<a href="index.jsp?main=settings">
		Settings
		</a>
		</td>
	</mm:compare>
	<mm:compare referid="main" value="settings">
		<th>
		<a href="index.jsp?main=settings">
		Settings
		</a>
		</th>
	</mm:compare>
  </tr>
</table>

