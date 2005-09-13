<mm:import externid="mode">packageinfo</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="assigned/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">
<tr>
	<th width="50%">
	Id
	</th>
	<th width="50%">
	Theme
	</th>
</tr>
<form action="<mm:url page="index.jsp" referids="main,id" />" method="post">
<TR>
		<td align="center">
			<input name="newid" size="30">
		</td>
		<td align="center">
			<select name="newtheme">
			<mm:nodelistfunction set="thememanager" name="getThemesList">
					<option selected><mm:field name="id" />	
			</mm:nodelistfunction>
			</select>
		</td>
</tr>
<tr>
  <table>
   <tr>
  <td valign="middle" align="center">
	<input type="hidden" name="action" value="addassign">
	<input type="submit" value="Save"></form>
  </td>
<form action="<mm:url page="index.jsp" referids="main" />" method="post">
  <td valign="middle" align="center">
	<input type="submit" value="Cancel"></form>
  </td>
  </tr>
  </table>
</tr>

</table>
