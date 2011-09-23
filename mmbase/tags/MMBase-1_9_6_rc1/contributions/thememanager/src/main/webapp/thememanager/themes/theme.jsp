<mm:import externid="mode">packageinfo</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="assigned/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="75%">
<tr>
	<th colspan="3" align="left">
	<a href="<mm:url page="index.jsp" referids="main" />">back</a>
	</th>
</tr>
<tr>
	<th width="33%">
	Theme	
	</th>
	<th width="33%">
	StyleSheets
	</th>
	<th width="33%">
	ImageSets
	</th>
</tr>
<mm:nodefunction set="thememanager" name="getThemeInfo" referids="id" >
<TR>
		<td alsign="center">
			<mm:field name="id" />
		</td>
		<td align="center">
			<mm:field name="stylesheetscount" />
		</td>
		<td align="center">
			<mm:field name="imagesetscount" />
		</td>
</tr>
<tr>
</tr>
 </mm:nodefunction>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th width="33%">
	StyleSheet
	</th>
	<th width="33%">
	Path
	</th>
	<th width="15">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="thememanager" name="getThemeStyleSheets" referids="id" >
<TR>
		<td alsign="center">
			<mm:field name="id" />
		</td>
		<td align="center">
			<mm:field name="path" />
		</td>
                <td width="15">
                        <A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="stylesheet" /><mm:param name="id" value="$id" /><mm:param name="cssid"><mm:field name="id" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
                </td>
</tr>
<tr>
</tr>
 </mm:nodelistfunction>
</table>



<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th width="33%">
	ImageSet	
	</th>
	<th width="33%">
	Image Count
	</th>
	<th width="15">
	&nbsp;
	</th>
</tr>
<mm:nodelistfunction set="thememanager" name="getThemeImageSets" referids="id" >
<TR>
		<td alsign="center">
			<mm:field name="id" />
		</td>
		<td align="center">
			<mm:field name="imagecount" />
		</td>
                <td width="15">
                        <A HREF="<mm:url page="index.jsp"><mm:param name="main" value="$main" /><mm:param name="sub" value="stylesheet" /><mm:param name="id" value="$id" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
                </td>
</tr>
<tr>
</tr>
 </mm:nodelistfunction>
</table>

