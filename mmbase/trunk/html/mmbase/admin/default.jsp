<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content type="text/html" expires="0">
<html>
  <head>
    <title>MMBase Administration</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css">
  </head>
  <mm:import externid="category" />
  <mm:import externid="subcategory" />
  <mm:notpresent referid="category">
    <frameset rows="70,*" border="0" frameborder="0" framespacing="0">
      <frame src="<mm:url page="nav.jsp?category=about&subcategory=about" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
      <frame src="<mm:url page="about/about.jsp"/>" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
    </frameset>
  </mm:notpresent>
  <mm:present referid="category">
    <frameset rows="70,*" border="0" frameborder="0" framespacing="0">
      <mm:notpresent referid="subcategory">
        <frame src="<mm:url page="nav.jsp?category=${category}&subcategory=${subcategory}" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
      </mm:notpresent>
      <mm:present referid="subcategory">
        <frame src="<mm:url page="nav.jsp?category=${category}" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
      </mm:present>
      <mm:compare referid="category" value="examples">
        <mm:import id="url">/mmexamples/index.jsp</mm:import>
      </mm:compare>
      <mm:compare referid="category" value="documentation">
        <mm:import id="url">/mmdocs/</mm:import>
      </mm:compare>
      <mm:notpresent referid="url">
        <mm:notpresent referid="subcategory">
          <mm:import id="url"><mm:write referid="category" />/main.jsp</mm:import>
        </mm:notpresent>
        <mm:present referid="subcategory">
          <mm:import id="url"><mm:write referid="category" />/<mm:write referid="subcategory" />.jsp</mm:import>
        </mm:present>
      </mm:notpresent>
      <frame src="<mm:url page="$url" />" name="main" scrolling="auto" marginwidth="1" marginheight="0" />
      </frameset>
  </mm:present>

<noframes>
<body class="basic">
<table summary="navigation">

<tr>
  <td class="data"><img src="../style/logo.gif" border="0" alt="MMBase">
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">About MMBase</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="/mmbase/mpl-1.0.html" />">License</a></td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="about/about.jsp" />">About</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">Demos</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="/mmexamples/index.jsp" />">Demos</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
  <th class="header">Editors</td>
</tr>
<tr>
  <td class="data"><a href="<mm:url page="editors/basic.jsp" />">Basic</a></td>
</tr>

<tr><td>&nbsp;</td></tr>

<tr>
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

<tr>
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
</mm:content>