<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="75%">
<tr>
	<th colspan="3">
	Readme first - MMBar explained
	</th>
</tr>
<tr>
	<td>
	<p>
	<font size="+1">MMBar is a simple test tool for testing your MMBase setup.  Its far from perfect and does not replace any website testing tool like jmeter. What it does allow you todo is compare different MMBase stacks in a easy way and compare changed you made in that stack (hardware, os, java-jvm ,database, mmbase).</font>
	</p>
	<p>
	<font size="+1">The most important tests are writing and reading nodes to and from the mmbase bridge with the cache turned off. First use the write tests to create 2000 to 3000 nodes. Then use the readtests to see how fast you can read them with 1,2 or 10 theads.</font>
	</p>
	<p>
	<font size="+1">We are still working on this so alot of options don't work yet so check cvs/website for updates of this contribution</font>
	</p>
	<p>
	<br />
	<br />
	<font size="+1">Daniel Ockeloen<br />
	MMCoder, daniel@xs4all.nl<br />
	</font>
	</p>
	</td>
</tr>
</table>
