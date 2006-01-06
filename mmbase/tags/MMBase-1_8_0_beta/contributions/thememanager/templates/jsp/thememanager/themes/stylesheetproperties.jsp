<mm:import externid="mode">packageinfo</mm:import>
<mm:import externid="cssid" />
<mm:import externid="tid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="themes/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="50%">
<tr>
	<th width="33%">
	Property
	</th>
	<th width="33%">
	Value
	</th>
	<th width="33%">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="thememanager" name="getStyleSheetProperties" referids="tid,cssid,id">
<form action="<mm:url page="index.jsp" referids="main,sub,cssid,tid,id" />" method="post">
<TR>
		<input type="hidden" name="action" value="setstylesheetproperty" />
		<td alsign="center">
			<mm:field name="name" />
			<input type="hidden" name="name" value="<mm:field name="name" />" />
		</td>
		<td align="center">
			<input name="value" value="<mm:field name="value" />" />
		</td>
		<td align="center">
			<input type="submit" value="change" />
		</td>
</tr>
</form>
<tr>
</tr>
</mm:nodelistfunction>
</table>
