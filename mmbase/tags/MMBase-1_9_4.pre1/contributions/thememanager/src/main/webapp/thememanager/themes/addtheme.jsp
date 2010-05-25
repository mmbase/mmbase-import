<mm:import externid="mode">packageinfo</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="assigned/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="45%">
<tr>
	<th colspan="3">
	Copy theme to a new one	
	</th>
</tr>
<form action="<mm:url page="index.jsp" referids="main,id" />" method="post">
<TR>
		<td align="center">
			<select name="copytheme">
			<mm:nodelistfunction set="thememanager" name="getThemesList">
					<option selected><mm:field name="id" />	
			</mm:nodelistfunction>
			</select>
		</td>
		<td align="center">
		        <input name="newtheme">
		</td>
		<td align="center">
		        <input type="hidden" name="action" value="copytheme">
			<input type="submit" value="copy">
		</td>
</tr>
</form>
</table>
