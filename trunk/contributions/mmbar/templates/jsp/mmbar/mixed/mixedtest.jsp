<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="mixed/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:import externid="name" />

<mm:nodefunction set="mmbar" name="getMixedTest" referids="name">
<mm:import id="state"><mm:field name="state" /></mm:import>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="55%">
<tr>
	<th colspan="4">
	Mixedtest : <mm:field name="name" />
	</th>
</tr>
<tr>
	<td colspan="4">
	<br />
	<mm:field name="description" />
	<br />
	<br />
	</td>
</tr>
<tr>
	<th>Name</th><td><mm:write referid="name" /></td>
	<th>OS</th><td><mm:field name="os" /></td>
</tr>
<tr>
	<th>State</th><td><mm:field name="state" /></td>
	<th>CPU</th><td><mm:field name="cpu" /></td>
</tr>
<tr>
	<th>Count</th><td><mm:field name="count" /></td>
	<th>Database</th><td><mm:field name="database" /></td>
</tr>
<tr>
	<th>Threads</th><td>1</td>
	<th>JDBC driver</th><td><mm:field name="driver" /></td>
</tr>
<tr>
	<th>Result</th><td><mm:field name="result" /> <mm:field name="resulttype" /></td>
	<th>Java version</th><td><mm:field name="java" /></td>
</tr>
<tr>
        <form action="<mm:url page="index.jsp" referids="main" />" method="post">
        <td align="center" colspan="4">
        <input type="hidden" value="none" name="sub" />
        <input type="submit" value="done" />
        </td>
        </form>
</tr>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="7">
	Speed compared to benchmark results for this test
	</th>
</tr>
<tr>
	<th>Result</th>
	<th>OS</th>
	<th>CPU</th>
	<th>Server</th>
	<th>Database</th>
	<th>Driver</th>
	<th>Java</th>
</tr>
<tr>
	<mm:field name="state">
	<mm:compare value="finished">
	<td><b><mm:field name="result" /> mmops</b></td>
	<td><b><mm:field name="os" /></b></td>
	<td><b><mm:field name="cpu" /></b></td>
	<td><b><mm:field name="server" /></b></td>
	<td><b><mm:field name="database" /></b></td>
	<td><b><mm:field name="driver" /></b></td>
	<td><b><mm:field name="java" /></b></td>
	</mm:compare>
	</mm:field>
</tr>
</mm:nodefunction>
<mm:nodelistfunction set="mmbar" name="getMixedTestBenchmarks" referids="name">
<tr>
	<td><mm:field name="result" /> mmops</td>
	<td><mm:field name="os" /></td>
	<td><mm:field name="cpu" /></td>
	<td><mm:field name="server" /></td>
	<td><mm:field name="database" /></td>
	<td><mm:field name="driver" /></td>
	<td><mm:field name="java" /></td>
</tr>
</mm:nodelistfunction>
</table>
