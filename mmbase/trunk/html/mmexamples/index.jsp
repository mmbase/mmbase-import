<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud jspvar="cloud">
<html>
<head>
<title>MMBase Demos</title>
<link rel="stylesheet" href="../css/mmbase.css" type="text/css">
</head>

<body >

<table align="center" width="97%" cellspacing="1" cellpadding="3" border="0">
<tr>
	<th class="main" colspan="3">MMBase Demos</th>
</tr>
<tr>
	<td colspan="3">
		<br />
            Here's a list of all working examples. Most of them require you to deploy an application, with
the same name as the example.<br />
		<br />
	</td>
</tr>

<tr><td colspan="3">&nbsp;</td></tr>

<tr>
	<th class="main" colspan="3">Jsp/Taglib Demo's</th>
</tr>
<tr>
	<th>Name demo</th>
	<th colspan="2">Description</th>
</tr>

<tr>
	<td>My News</td>
	<td>
		 Small example of a news/magazine system
	</td>
	<td class="link" >
		<a href="<mm:url page="jsp/mynews.jsp" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>Taglib</td>
	<td>
		A lot of different examples for the MMBase taglib.
	</td>
	<td class="link" >
		<a href="<mm:url page="taglib/" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>My editors</td>
	<td>
		Alternative generic editors
	</td>
	<td class="link" >
		<a href="<mm:url page="jsp/my_editors/" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr>
	<td>Community (requires 'community' module from mmbase-community.jar)</td>
	<td>
    Example of the community-features of MMBase (forum &amp; chat)
	</td>
	<td class="link" >
		<a href="<mm:url page="jsp/community.jsp" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>


<tr><td colspan="3">&nbsp;</td></tr>

<tr>
	<th class="main" colspan="3">Other Demo's</th>
</tr>

<tr>
	<th>Name demo</th>
	<th colspan="2">Description</th>
</tr>

<tr>
	<td>Editwizard</td>
	<td>
		Different editwizard-examples. TODO: you need editwizards installed!
	</td>
	<td class="link" >
		<a href="<mm:url page="editwizard/" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>


<tr>
	<td>Codings</td>
	<td>
		Shows text in different encodings.
	</td>
	<td class="link" >
		<a href="<mm:url page="codings/" />"><img src="../mmadmin/jsp/images/next.gif" border="0" align="left" /></a>
	</td>
</tr>

<tr><td colspan="3">&nbsp;</td></tr>

</table>
<a href="<mm:url page=".." />"> back</a>
</body>
</html>
</mm:cloud>
