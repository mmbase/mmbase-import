<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html>
<head>
   <title>MMBase Administration</title>
   <link rel="stylesheet" href="css/mmbase.css" type="text/css">
</head>
<%
    String menu=request.getParameter("menu");
    String submenu=request.getParameter("submenu");
    String category=request.getParameter("category");
    String subcategory=request.getParameter("subcategory");
    if (category==null) {
%>
	<frameset rows="60,*" border="0" frameborder="0" framespacing="0" >
   		<frame src="nav.jsp?menu=1&submenu=1" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
   		<frame src="about/license.jsp" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
	</frameset>
<% } else { %>
	<frameset rows="60,*" border="0" frameborder="0" framespacing="0">
   		<frame src="nav.jsp?menu=<%=menu%>&submenu=<%=submenu%>" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
   		<% if (subcategory==null) { %>
	  	  <frame src="<%=category%>/main.jsp" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        <% } else { %>
		  <frame src="<%=category%>/<%=subcategory%>.jsp" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        <% } %>		
	</frameset>
<% } %>

<noframes>
<body class="basic">
<table summary="navigation" width="93%" cellspacing="1" cellpadding="3" border="0">

<tr>
  <td class="data"><img src="images/logo.gif" border="0" alt="MMBase">
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">About MMBase</td>
</tr>
<tr>
  <td class="data"><a href="about/license.jsp">License</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Editors</td>
</tr>
<tr>
  <td class="data"><a href="editors/basic.jsp">Basic</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Administration</td>
</tr>
<tr>
  <td class="data"><a href="admin/servers.jsp">Servers</a></td>
</tr>
<tr>
  <td class="data"><a href="admin/builders.jsp">Builders</a></td>
</tr>
<tr>
  <td class="data"><a href="admin/applications.jsp">Applications</a></td>
</tr>
<tr>
  <td class="data"><a href="admin/modules.jsp">Modules</a></td>
</tr>
<tr>
  <td class="data"><a href="admin/databases.jsp">Databases</a></td>
</tr>
<tr>
  <td class="data"><a href="admin/documentation.jsp">Documentation</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Tools</td>
</tr>
<tr>
  <td class="data"><a href="tools/cache.jsp">Cache Monitor</a></td>
</tr>
<tr>
  <td class="data"><a href="tools/email.jsp">Email Queue Monitor</a></td>
</tr>
</table>
</body>
</noframes>

</html>
