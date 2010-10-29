<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="principlesets/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<tr>
	<th left" colspan="5">
	Principle Sets defined 
        </tr>
	</th>
</tr>
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>Alias</th>
	<th width="10">&nbsp;</th>
	<th width="10">&nbsp;</th>
<tr>
 <mm:listnodes type="principlesets">
  <tr>
     <td align="left"><a href="<mm:url page="index.jsp"><mm:param name="main">principles</mm:param><mm:param name="principleset"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="name" /></a></td>
     <td align="left"><a href="<mm:url page="index.jsp"><mm:param name="main">principles</mm:param><mm:param name="principleset"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="description" /></a></td>
     <td align="left"><a href="<mm:url page="index.jsp"><mm:param name="main">principles</mm:param><mm:param name="principleset"><mm:field name="number" /></mm:param></mm:url>"><mm:aliaslist><mm:write /></mm:aliaslist></a></td>
     <td align="center" width="15"><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">editprincipleset</mm:param><mm:param name="principleset"><mm:field name="number" /></mm:param></mm:url>"><img src="images/mmbase-edit.gif" border="0" align="right"></a></td>
     <td align="center" width="15"><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">deleteprincipleset</mm:param><mm:param name="principleset"><mm:field name="number" /></mm:param></mm:url>"><img src="images/mmbase-delete.gif" border="0" align="right"></a></td>
  </tr>
 </mm:listnodes>
<tr>
        <tr>
	   <td colspan="5"><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">newprincipleset</mm:param></mm:url>"><img src="images/mmbase-new.gif" border="0" align="right"></a></td>
        </tr>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="65%">
<tr>
	<th align="left">
		Tip :
	</th>
</tr>
<tr>
	<td align="left">
In most cases you will only have 1 set of principles (for example the princples for the MMBase Opensource Project). <br /><br  />
This applications allows you to create more for example if you want a test set or are working on a whole new set and don't want to mix them just yet.<br /><br />
 But if you (most likely) only have one (or a prefered one) you can add a alias called 'Principle.default' and this application will open not on this page but on the principles page with that set as the default.<br /><br /><br />
	Have fun with this contribution,<br /><br />
	Daniel Ockeloen<br />
	MMCoder.<br /><br />
	</td>
</tr>
</table>
