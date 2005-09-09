<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>
<html>
<head>
<title></title>
</head>
<body>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids" />

<table width="100%" height="90%" align="CENTER" cellspacing="0" cellpadding="0" border="0">
	<tr><td height="28" colspan="2"><iframe width="100%" height="28" frameborder="1" marginheight="0" marginwidth="0" scrolling="no" src="<mm:treefile write="true" page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />"></iframe></td></tr>
   <tr>
      <td height="90%" width="20%" valign="MIDDLE"><iframe src="<mm:treefile write="true" page="/education/wizards/code.jsp" objectlist="$includePath" />"    id="menu" name="menu" width="100%" height="90%" marginwidth="0" marginheight="0" border="1"></iframe></td>
      <td width="80%"><iframe src="<mm:treefile write="true" page="/education/wizards/loading.jsp" objectlist="$includePath" />" id="text" name="text" width="100%" height="90%" marginwidth="0" marginheight="0" border="1"></iframe></td>
   </tr>
</table>

</body>
</html>
</mm:cloud>
</mm:content>