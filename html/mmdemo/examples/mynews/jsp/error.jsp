<%@ page isErrorPage="true" import="java.util.*" %>
<html>
<head>
<title>News error</title>
</head>
<body>

<table border="0" cellspacing="0" cellpadding="5">
<tr>
<td width="150" align="right"> &nbsp; </td>
<td align="right" valign="bottom"><h1>News error page</h1> </td>
</tr>

<tr>
<td width="150" align="right"> &nbsp; </td>
<td align="right"> <b>Oops! an error occurred.</b> Is the MyNews application installed?</td>
</tr>

<tr>
<td width="150" align="right"> &nbsp; </td>
<td align="right"> <%= exception.getMessage() %>.  </td>
</tr>
</table>
</html>
