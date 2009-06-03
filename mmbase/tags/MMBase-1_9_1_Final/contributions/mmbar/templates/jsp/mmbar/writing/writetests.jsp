<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="writing/actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:nodefunction set="mmbar" name="getStateInfo">
<mm:import id="state"><mm:field name="state" /></mm:import>
<mm:compare value="waiting" referid="state">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="55%">
<tr>
	<th colspan="5">
	Write tests
	</th>
</tr>
<tr>
	<th>Name</th>
	<th>State</th>
	<th>Result</th>
	<th>Info</th>
	<th>Action</th>
</tr>
  <mm:nodelistfunction set="mmbar" name="getWriteTests">
  <tr>
	<th>
	<mm:field name="name" />
	</th>
	<td width="100" align="center">
	<mm:field name="state" />
	</td>
	<td width="100" align="center">
	<mm:field name="result" /> <mm:field name="resulttype" />
	</td>
	<form action="<mm:url page="index.jsp" referids="main" />" method="post">
	<td width="100" align="center">
	<input type="hidden" value="writetest" name="sub" />
	<input type="hidden" value="<mm:field name="name" />" name="name" />
	<input type="submit" value="info" />
	</td>
	</form>
	<form action="<mm:url page="index.jsp" referids="main" />" method="post">
	<td width="100" align="center">
	<input type="hidden" value="performwritetest" name="action" />
	<input type="hidden" value="<mm:field name="name" />" name="name" />
	<input type="submit" value="<mm:field name="action" />" />
	</td>
	</form>
   </tr>
  </mm:nodelistfunction>
</table>
  </mm:compare>
  <mm:compare value="running" referid="state">
<script language="JavaScript">
<!--

function doLoad()
{
    // the timeout value should be the same as in the "refresh" meta-tag
    setTimeout( "refresh()", 2000 );
}

function refresh()
{
    window.location.href ='index.jsp?main=writing';
}
//-->
</script>
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="55%">
		<tr>
			<th colspan="2">
				Running <mm:field name="runningname" />
			</th>
		</tr>
		<tr>
			<th colspan="2">
				Pos <mm:field name="runningpos" /> of <mm:field name="runningcount" /> 
			</th>
		</tr>
		<tr>
		<td width="<mm:field name="progressbar" />%" style="background-color: #00aa00">&nbsp;</td><td>&nbsp;</td>
		</tr>
	</table>
  </mm:compare>
</mm:nodefunction>
