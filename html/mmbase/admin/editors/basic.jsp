<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<mm:cloud name="mmbase" method="asis">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>MMBase Basic Editors</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic" >

<table summary="license information">
<tr>
  <th class="header" colspan="2">MMBase Basic Editors</th>
</tr>
<tr>
  <td class="multidata" colspan="2">
  <p>MMBase is based on the single idea of creating a object cloud that represents what you want to share with people.<br />
  We create objects like images, newsitems, or urls and store them as objects in MMBase. We then create relations between them.<br />
  This makes it possible for MMBase to generate basic (generic) editors, since they all have the same basic idea of how an object
  is represented, as well as the relations to other objects.<br />
  The Basic editors are ment primarily for site administrators. For your users you are encouraged to set up the task-based editwizards.<br />
  In the urls given below we have setup an admin account so you can edit/change the objects within the installed cloud.<br />
  Note that in order to use the SCAN editors you need to have installed the SCAN scripting language.
  </p>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <th class="header" colspan="2">URL</th>
</tr>
<tr>
  <td class="linkdata" colspan="2"><a href="<mm:url page="/mmeditors/jsp/" />">The MMBase Editors (jsp) : http://host:port/mmeditors/jsp/</a></td>
</tr>
<tr>
  <td class="linkdata" colspan="2"><a href="<mm:url page="/mmexamples/jsp/my_editors/" />">Alternate Editors (jsp) : http://host:port/mmexamples/jsp/my_editors/</a></td>
</tr>
<tr><td>&nbsp;</td></tr>

<tr class="footer">
<td class="navigate"><a href="<mm:url page="../default.jsp" />" target="_top"><img src="../images/back.gif" alt="back" border="0" /></td>
<td class="data">Return to home page</td>
</tr>
</table>
</body></html>
</mm:cloud>
