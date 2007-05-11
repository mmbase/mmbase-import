<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content type="text/html" expires="0">
<html>
  <head>
    <title>MMBase Administration</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css">
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
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
        <frame src="<mm:url referids="category,subcategory" page="nav.jsp" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
      </mm:notpresent>
      <mm:present referid="subcategory">
        <frame src="<mm:url referids="category,subcategory" page="nav.jsp" />" name="navigatie" scrolling="no" marginwidth="1" marginheight="0" />
      </mm:present>
      <mm:compare referid="category" value="examples">
        <mm:import id="url">/mmexamples/</mm:import>
      </mm:compare>
      <mm:compare referid="category" value="documentation">
        <mm:import id="url">/mmdocs/</mm:import>
      </mm:compare>
      <mm:notpresent referid="url">        
        <mm:import externid="url" />
      </mm:notpresent>
      <mm:notpresent referid="url">
        <mm:remove referid="url" />
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
      <tr><td class="data"><img src="../style/logo.gif" border="0" alt="MMBase" /></td></tr>      

      <tr><th class="header">About MMBase</th></tr>
      <tr><td class="data"><a href="<mm:url page="/mmbase/mpl-1.0.html" />">License</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="about/about.jsp" />">About</a></td></tr>


      <tr><th class="header">Demos</th></tr>
      <tr><td class="data"><a href="<mm:url page="/mmexamples/" />">Demos</a></td></tr>


      <tr><th class="header">Editors</th></tr>
      <tr>
         <td class="data"><a href="<mm:url page="editors/basic.jsp" />">Generic</a></td>
         <td class="data"><a href="<mm:url page="editors/specific.jsp" />">Specific</a></td>
      </tr>


      <tr><th class="header">Administration</th></tr>
      <tr><td class="data"><a href="<mm:url page="admin/resourceedit.jsp" />">Resources</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/servers.jsp" />">Servers</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/builders.jsp" />">Builders</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/applications.jsp" />">Applications</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/modules.jsp" />">Modules</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/databases.jsp" />">Databases</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="admin/documentation.jsp" />">Documentation</a></td></tr>

      <tr><th class="header">Tools</th></tr>
      <tr><td class="data"><a href="<mm:url page="tools/cache.jsp" />">Cache Monitor</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="tools/querytool.jsp" />">Raw SQL tool</a></td></tr>
      <tr><td class="data"><a href="<mm:url page="tools/email.jsp" />">Email Queue Monitor</a></td></tr>
    </table>
  </body>
</noframes>

</html>
</mm:content>