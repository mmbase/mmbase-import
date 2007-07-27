<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="assigned/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">
<tr>
	<th>
	Id
	</th>
	<th>
	Theme
	</th>
	<th>
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="thememanager" name="getAssignedList">
<TR>
		<td>
			<mm:import id="pid"><mm:field name="id" /></mm:import>
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="assign" /><mm:param name="id" value="$pid" /></mm:url>"><mm:field name="id" /></a>
		</td>
		<td>
			<mm:field name="theme" />	
		</td>
		<td width="15">
			<A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="assign" /><mm:param name="id" value="$pid" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
		</td>
		<mm:remove referid="pid" />
</tr>
 </mm:nodelistfunction>

<tr>
 <td colspan="5" align="right">
 Add Assign
 <A HREF="<mm:url page="index.jsp">
	<mm:param name="main" value="$main" />
	<mm:param name="sub" value="addassign" />
	</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0"></A>
  </td>

</table>
