<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page language="java" contentType="text/html; charset=utf-8"

%><mm:cloud
><mm:import externid="image">-1</mm:import
><mm:node number="$image">
<head>
<title><mm:field name="title" /></title>
<link rel="stylesheet" type="text/css" href="/css/mmbase-dev.css">
<meta http-equiv="imagetoolbar" content="no">
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><img src="../media/spacer.gif" width="10" height="10"></td>
</tr>
<tr>
	<td><table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="2"><a href="#" onClick="window.close()" title="Click on image to close window">
				<img src="<mm:image template="s(600)" />" width="600" border="0"></a></td>
		</tr>
		<tr>
			<td colspan="2"><img src="../media/spacer.gif" width="10" height="10" border="0"></td>
		</tr>
		<tr valign="top">
			<td>
			<mm:field name="title" jspvar="images_title" vartype="String" write="false"
				><% if(!images_title.equals("")&&images_title.indexOf("#NZ#")==-1) {
					%><b><%= images_title %></b><br /><%
				} %></mm:field
			><mm:field name="description" /></td>
			<td align="right"><a href="javascript:window.close();">close this window</a></td>
		</tr>
	</table></td>
</tr>
</table>
</body>
</html>
</mm:node>
</mm:cloud>
