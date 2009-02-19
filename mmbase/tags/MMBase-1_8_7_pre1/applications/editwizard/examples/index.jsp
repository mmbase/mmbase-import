<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html">
<mm:cloud>
<html>
<head>
  <title>MMBase EditWizard Examples</title>
  <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
  <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
  <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
</head>

<body >
  <table>
    <tr>
      <th class="main" colspan="3">MMBase EditWizard Examples</th>
    </tr>
    <tr>
      <td colspan="3">
        <p>
          Lots of these examples edit data from the <a href="<mm:url page="/mmexamples/mynews/" />">My News example</a>.
        </p>
      </td>
    </tr>

    <tr>
      <th class="header" colspan="3">Wizards of various complexity</th>
    </tr>
    <tr>
      <th>Name</th>
      <th colspan="2">Description</th>
    </tr>

    <tr>
      <td>Simple Wizards</td><td>A very simple example.</td>
      <td class="link" >
        <a href="<mm:url page="simple/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Wizards in Dutch</td><td>Also very simple. Shows how to internationalize (to dutch),</td>
      <td class="link" >
        <a href="<mm:url page="dutch/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Adding a Logo</td><td>This example shows how to add a logo</td>
      <td class="link" >
        <a href="<mm:url page="logo/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>New Field Format Type</td><td>This example shows how to create a new ftype (field format type)</td>
      <td class="link" >
        <a href="<mm:url page="newftype/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Advanced Wizards</td><td>Some more sophisticated examples</td>
      <td class="link" >
        <a href="<mm:url page="advanced/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Calling Other Wizards</td><td>Examplkes containing all kinds of wizard calls ('start-wizards').</td>
      <td class="link" >
        <a href="<mm:url page="startwizard/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>

    <tr>
      <th class="header" colspan="3">Wizards with alternate styles</th>
    </tr>
    <tr>
      <th>Name</th>
      <th colspan="2">Description</th>
    </tr>

    <tr>
      <td>Alternate Look: PO</td><td>Different wizard styles: "PO" style (using alternate images and CSS)</td>
      <td class="link" >
        <a href="<mm:url page="po/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Alternate Look: Preditor</td><td>Different wizard styles: "preditor" style (using alternate images, CSS, and xsl sheet)</td>
      <td class="link" >
        <a href="<mm:url page="preditor/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>
    <tr>
      <td>Alternate Look: Finalist</td><td>Different wizard styles: "finalist" style (using alternate images, CSS, javascript and xsl sheet)</td>
      <td class="link" >
        <a href="<mm:url page="finalist/" />"><img alt="&gt;" src="<mm:url page="/mmbase/style/images/next.gif" />" /></a>
      </td>
    </tr>

  </table>
  <div class="link">
    <a href="<mm:url page=".." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</body>
</html>
</mm:cloud>
</mm:content>
