<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>License Information</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic" >
<table summary="license information" width="93%" cellspacing="1" cellpadding="3" border="0">
<tr align="left">
  <th class="header" colspan="2">License Information & Thanks to</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
  <p>MMBase is released as an Open Source product (see the main website at <a href="http://www.mmbase.org">http://www.mmbase.org</a>).<br />
     The MMRunner version of MMbase includes an application server and a database.
     Since we wanted a simple and 100% java solution we used Orion and Hypersonic, but
     MMBase itself supports many more both commercial and opensource products.
  </p>

  <p>About Orion :</p><p>
     Orion is a great j2ee application server that is 100% java based, easy to install and follows all the new and hip specs.
     Its not free but at $1500 its very cheap. It is free for development and non-profit use.<br />
     Many thanks to the Orion people for allowing us to include it in the runner version.<br />
     More information on Orion can be found at <a href="http://www.orionserver.com">http://www.orionserver.com</a>.
  </p>

  <p>About Hypersonic :</p><p>
	Hypersonic is a small 100% java based JDBC database.
	It is open source, fast, and very small (about 100kb).
	It also supports a memory only mode, used in the demo runner, which means that it is empty once you stop the server.<br />
	More information on Hypersonic can be found at <a href="http://hsql.oron.ch">http://hsql.oron.ch</a>.
  </p>
  &nbsp;
  </td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
<td class="navigate"><a href="../default.jsp" target="_top"><img src="../images/back.gif" alt="back" border="0" align="left" /></td>
<td class="data">Return to home page</td>
</tr>

</table>
</body></html>
</mm:cloud>