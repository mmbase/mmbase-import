<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="providers/actions.jsp" />
</mm:present>
<!-- end action check -->
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
<tr>
<td>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 100px;" border="1" width="80%">
<form action="<mm:url page="index.jsp" referids="main,sub" />" method="post">
<tr>
	<th COLSPAN="2">
	Method 1 (http): enter the share subscribe url provided to you
	</th>
</tr>
<tr>
  <th width="100">
	subscribe url
  </th>
  <td>
	<input name="url" size="60">
  </td>
</tr>
<tr>
  <th width="100">
  </th>
  <td>
	<input type="hidden" name="action" value="addsubscribeprovider">
	<center><input type="submit" value="Add Provider"></center>
  </td>
</tr>
</form>
</table>
</td>
</tr>


<tr>
<td>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left: 100px;margin-top: 20px;" width="80%">
<form action="<mm:url page="index.jsp" referids="main,sub" />" method="post">
<tr>
	<th colspan="2">
	Method 2 (disk): enter a name and path for the share 
	</th>
</tr>
<tr>
  <th width="100">
	name
  </th>
  <td>
	<input name="name" size="20">
  </td>
</tr>
<tr>
  <th width="100">
	path
  </th>
  <td>
	<input name="path" size="60">
  </td>
</tr>
<tr>
  <th>
	&nbsp;
  </th>
  <td>
	<input type="hidden" name="action" value="adddiskprovider">
	<center><input type="submit" value="Add Provider"></center>
  </td>
</tr>
</form>
</table>
</td>
</tr>
<tr>
<td>
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 100px;margin-top: 20px;" border="1" width="80%">
<form action="<mm:url page="index.jsp" referids="main,sub" />" method="post">
<tr>
	<th COLSPAN="2">
	Method 3 (http) : Some well known mmbase partner servers
	</th>
</tr>
<tr>
  <th width="100">
	subscribe url
  </th>
  <td>
	<select name="url">
		<option value="http://packages.submarine.nl">Submarine Package Server
		<option value="http://packages.mmbase.org">MMBase.org Package Server
	</select>
  </td>
</tr>
<tr>
  <th width="100">
  </th>
  <td>
	<input type="hidden" name="action" value="addsubscribeprovider">
	<center><input type="submit" value="Add Provider"></center>
  </td>
</tr>
</form>
</table>
</td>
</tr>
</TABLE>

