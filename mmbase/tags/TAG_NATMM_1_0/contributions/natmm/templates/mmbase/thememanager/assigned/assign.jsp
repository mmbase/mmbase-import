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
<mm:nodefunction set="thememanager" name="getAssignInfo" referids="id" >
<TR>
		<td>
			<mm:write referid="id" />
		</td>
		<td>
			<mm:import id="curtheme"><mm:field name="theme" /></mm:import>
			<select name="newtheme">
			<mm:nodelistfunction set="thememanager" name="getThemesList">
				<mm:field name="id">
					<mm:compare referid2="curtheme">
					<option selected><mm:field name="id" />	
					</mm:compare>
					<mm:compare referid2="curtheme" inverse="true">
					<option><mm:field name="id" />	
					</mm:compare>
				</mm:field>
			</mm:nodelistfunction>
			</select>
		</td>
</tr>
<tr>
  <table>
   <tr>
  <td valign="middle" align="center">
	<input type="hidden" name="action" value="changeassign">
	<input type="submit" value="Save"></form>
  </td>
<form action="<mm:url page="index.jsp" referids="main" />" method="post">
  <td valign="middle" align="center">
	<input type="submit" value="Cancel"></form>
  </td>
	<form action="<mm:url page="index.jsp" referids="main" />" method="post">
	<input type="hidden" name="action" value="removeassign">
	<input type="hidden" name="removeid" value="<mm:write referid="id" />">
  <td valign="middle" align="center">
	<input type="submit" value="Delete"></form>
  </td>
  </tr>
  </table>
</tr>
 </mm:nodefunction>

</table>
