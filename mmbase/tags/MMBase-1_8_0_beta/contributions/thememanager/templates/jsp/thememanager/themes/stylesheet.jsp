<mm:import externid="mode">packageinfo</mm:import>
<mm:import externid="cssid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="assigned/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="50%">
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
<mm:nodelistfunction set="thememanager" name="getStyleSheetClasses" referids="id,cssid">
<TR>
		<td alsign="center">
			<mm:field name="id" />
		</td>
		<td align="center">
			<mm:field name="propertycount" />
		</td>
                <td width="15">
                <A HREF="<mm:url page="index.jsp" referids="main,cssid"><mm:param name="sub" value="stylesheetproperties" /><mm:param name="tid" value="$id" /><mm:param name="id"><mm:field name="id" /></mm:param></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
                </td>
</tr>
<tr>
</tr>
</mm:nodelistfunction>
</table>
