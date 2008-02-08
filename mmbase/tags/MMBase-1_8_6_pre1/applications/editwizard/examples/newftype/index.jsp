<html>
<head>
    <title>EditWizard samples</title>
    <link rel="stylesheet" type="text/css" href="../style.css" />
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
   <!-- Very straightforward example -->
</head>
<body>
<form>
<mm:cloud  rank="basic user"><!-- make sure the user is logged in, because editwizard sometimes is buggy -->
    <h1>Editwizard - samples</h1>
  <p>
   This example adds a new ftype to the default editwizard XSL's.
  </p>
    <table>
  <mm:import id="referrer"><%=new  java.io.File(request.getServletPath())%></mm:import>
  <tr><td>
    <a href="<mm:url referids="referrer" page="/mmbase/edit/wizard/jsp/list.jsp?wizard=tasks/people&nodepath=people&fields=firstname,lastname,owner" />" >Person-Test</a>
  </td><td>
  A person editor with a field with a custom defined ftype.
  </td><td><a target="_new" href="<mm:url page="../citexml.jsp"><mm:param name="page">newftype/tasks/people.xml</mm:param></mm:url>">view XML</a></td></tr>
  </table>
  <hr />

  <a href="../">back</a>
  <hr />
  <mm:cloudinfo type="user" /> / <mm:cloudinfo type="rank" />
 </mm:cloud>
</form>
</body>
</html>