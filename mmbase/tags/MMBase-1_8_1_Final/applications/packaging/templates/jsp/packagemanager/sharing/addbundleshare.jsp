<mm:import externid="version">best</mm:import>
<mm:import externid="provider">ignore</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="sharing/actions.jsp" />
</mm:present>
<!-- end action check -->


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="45%">
<form action="<mm:url page="index.jsp" referids="main" />" method="post">
		  <tr>
		  <th>
			Add Share from available bundles
		  </th>
		  </tr>
		  <tr>
		  <td align="middle">
			<select name="newshare">
				<mm:nodelistfunction set="mmpm" name="getNotSharedBundles">
					<mm:first><mm:import id="foundnonshared">true</mm:import></mm:first>
					<option value="<mm:field name="id" />"> '<mm:field name="name" />' &nbsp;&nbsp; (<mm:field name="type" />) from '<mm:field name="maintainer" />'
				</mm:nodelistfunction>
			</select>
			<input type="hidden" name="action" value="addbundleshare">
		  </td>
		  </tr>
		  <tr>
		  <td align="middle">
			<mm:present referid="foundnonshared">
			<input type="submit" value="add bundle as a share">
			</mm:present>
			<mm:present referid="foundnonshared" inverse="true">
			All are shared allready
			</mm:present>
		  </td>
		  </tr>
</form>
</table>


