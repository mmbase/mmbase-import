<mm:import externid="mode">packageinfo</mm:import>
<mm:import externid="cssid" />
<mm:import externid="searchkey">*</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="themes/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="50%">
<tr>
	<th colspan="3" align="left">
	<a href="<mm:url page="index.jsp" referids="main,id"><mm:param name="sub">theme</mm:param></mm:url>">back</a>
	</th>
</tr>
<tr>
	<th width="50%">
	ClassName	
	</th>
	<th width="50%">
	Property Count		
	</th>
	<th width="15">
	Edit		
	</th>
</tr>
<form action="<mm:url page="index.jsp" referids="main,sub,cssid,id" />" method="post">
<tr>
	<th width="50%" align="left">
	Search <input size="15" name="searchkey" value="<mm:write referid="searchkey" />" />
	</th>
	<th width="50%">
	&nbsp;
	</th>
	<th width="15">
	&nbsp;
	</th>
</tr>
</form>

<mm:nodelistfunction set="thememanager" name="getStyleSheetClasses" referids="id,cssid,searchkey">
<TR>
		<td alsign="center">
			<mm:field name="id" />
		</td>
		<td align="center">
			<mm:field name="propertycount" />
		</td>
                <td width="15">
                <A HREF="<mm:url page="index.jsp" referids="main,cssid,searchkey"><mm:param name="sub" value="stylesheetproperties" /><mm:param name="tid" value="$id" /><mm:param name="id"><mm:field name="id" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
                </td>
</tr>
<tr>
</tr>
</mm:nodelistfunction>
<form action="<mm:url page="index.jsp" referids="main,sub,searchkey,cssid,id" />" method="post">
                <input type="hidden" name="action" value="addstylesheetclass" />
<tr>
<td><input name="name" /></td><td>&nbsp;</td><td><input type="submit" value="add" /></td>
</tr>
</form>
</table>
