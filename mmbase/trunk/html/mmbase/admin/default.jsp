<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<head>
   <title>MMBase Administration</title>
   <link rel="stylesheet" href="css/mmbase.css" type="text/css">
    <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</head>
<%
    String category=request.getParameter("category");
    String subcategory=request.getParameter("subcategory");
    if (category==null) {
%>
        <frameset rows="60,*" border="0" frameborder="0" framespacing="0" >
                <frame src="<mm:url page="nav.jsp?category=about&subcategory=about"/>" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
                <frame src="<mm:url page="about/about.jsp"/>" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        </frameset>
<% } else { %>
        <frameset rows="60,*" border="0" frameborder="0" framespacing="0">
                <frame src="<mm:url page="<%="nav.jsp?category="+category+"&subcategory="+subcategory%>" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
        <% if (category.equals("examples")) { %>
                  <frame src="<mm:url page="/mmexamples/index.jsp" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        <% } else if (category.equals("documentation")) { %>
                  <frame src="<mm:url page="/mmdocs/index.html" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        <% } else if (subcategory==null) { %>
                  <frame src="<mm:url page="<%=category+"/main.jsp"%>" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
        <% } else { %>
                  <frame src="<mm:url page="<%=category+"/"+subcategory+".jsp"%>" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
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
  <td class="data"><a href="<mm:url page="about/license.jsp" />">License</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="about/about.jsp" />">About</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Demos</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="/mmexamples/index.jsp" />">Demos</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Editors</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="editors/basic.jsp" />">Basic</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Administration</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/servers.jsp" />">Servers</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/builders.jsp" />">Builders</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/applications.jsp" />">Applications</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/modules.jsp" />">Modules</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/databases.jsp" />">Databases</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="admin/documentation.jsp" />">Documentation</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr align="left">
  <th class="header">Tools</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="tools/cache.jsp" />">Cache Monitor</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="tools/email.jsp" />">Email Queue Monitor</a></td>
</tr>
</table>
</body>
</noframes>

</html>
