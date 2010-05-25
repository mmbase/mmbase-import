<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="weblogs/actions.jsp" />
</mm:present>
<!-- end action check -->

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="80%">
<tr>
	<th left" colspan="5">
	Weblogs defined 
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
 <mm:listnodes type="weblogs">
  <tr>
     <td><a href="<mm:url page="index.jsp"><mm:param name="main">weblog</mm:param><mm:param name="weblogid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="name" /></a></td>
     <td><a href="<mm:url page="index.jsp"><mm:param name="main">weblog</mm:param><mm:param name="weblogid"><mm:field name="number" /></mm:param></mm:url>"><mm:field name="body" /></a></td>
     <td><a href="<mm:url page="index.jsp"><mm:param name="main">weblog</mm:param><mm:param name="weblogid"><mm:field name="number" /></mm:param></mm:url>"><mm:aliaslist><mm:write /></mm:aliaslist></a></td>
     <td><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">editweblog</mm:param><mm:param name="weblogid"><mm:field name="number" /></mm:param></mm:url>"><img src="images/mmbase-edit.gif" border="0" align="right"></a></td>
     <td><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">deleteweblog</mm:param><mm:param name="weblogid"><mm:field name="number" /></mm:param></mm:url>"><img src="images/mmbase-delete.gif" border="0" align="right"></a></td>
  </tr>
 </mm:listnodes>
<tr>
        <tr>
	   <td colspan="5"><a href="<mm:url page="index.jsp" referids="main"><mm:param name="sub">newweblog</mm:param></mm:url>"><img src="images/mmbase-new.gif" border="0" align="right"></a></td>
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
	<td>
This application allows you to create multiple blogs (so you can run one server that hosts the blogs of all your friends). <br /><br />
But if you only have one (or a prefered one) you can add a alias called 'MMBlog.default' and this application will open not on this page but on the weblog page with that weblog as the default.<br /><br /><br />
	Have fun with this contribution,<br /><br />
	Daniel Ockeloen<br />
	MMCoder.<br /><br />
	</td>
</tr>
</table>
