<mm:import externid="mode">packageinfo</mm:import>
<mm:import externid="cssid" />
<mm:import externid="tid" />
<mm:import externid="searchkey">*</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="themes/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="50%">
<tr>
	<th colspan="4" align="left">
	<a href="<mm:url page="index.jsp" referids="main,id@tid,tid@id,cssid,searchkey"><mm:param name="sub">stylesheet</mm:param></mm:url>">back</a>
	</th>
</tr>
<tr>
	<th width="25%">
	Property
	</th>
	<th width="25%">
	Value
	</th>
	<th width="25%">
	&nbsp;
	</th>
	<th width="25%">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="thememanager" name="getStyleSheetProperties" referids="tid,cssid,id">
<TR>
<form action="<mm:url page="index.jsp" referids="main,sub,cssid,tid,id,searchkey" />" method="post">
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
</form>
<form action="<mm:url page="index.jsp" referids="main,sub,cssid,tid,id,searchkey" />" method="post">
		
		<td align="center">
		<input type="hidden" name="name" value="<mm:field name="name" />" />
		<input type="hidden" name="action" value="removestylesheetproperty" />
		<input type="submit" value="delete" />
		</td>
</form>
</tr>
<tr>
</tr>
</mm:nodelistfunction>
<form action="<mm:url page="index.jsp" referids="main,sub,cssid,tid,id,searchkey" />" method="post">
<tr>
	<input type="hidden" name="action" value="addstylesheetproperty" />
	<td align="center"><input name="name" /></td>
	<td align="center"><input name="value" /></td>
	<td align="center"><input type="submit" value="add" /></td>
	<td align="center">&nbsp;</td>
</tr>
</form>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="50%">
<form action="<mm:url page="index.jsp" referids="main,cssid,id@name,tid@id,searchkey"><mm:param name="sub">stylesheet</mm:param></mm:url>" method="post">
<tr>
	<th colspan="2">delete whole class ? </th><td align="center"><select name="confirm"><option>No<option>Yes</select></td><td align="center"><input type="submit" value="perform" /></td>
</tr>
	<input type="hidden" name="action" value="removestylesheetclass" />
</form>
</table>



