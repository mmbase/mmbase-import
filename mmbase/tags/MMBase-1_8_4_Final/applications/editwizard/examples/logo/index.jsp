<html>
<head>
    <title>EditWizard samples</title>
    <link rel="stylesheet" type="text/css" href="../style.css" />
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
   <!-- Very straightforward example -->
</head>
<body>
<form>
<mm:cloud rank="basic user"><!-- make sure the user is logged in, because	editwizard sometimes is buggy -->
  <h1>Editwizard - samples</h1>
  <p>
    This example adds a logo to the default editwizard XSL's.
  </p>
  <table>
    <mm:import id="referrer"><%=new  java.io.File(request.getServletPath())%></mm:import>
    <tr><td>
    <a href="<mm:url referids="referrer" page="/mmbase/edit/wizard/jsp/list.jsp?wizard=../samples/people&nodepath=people&fields=firstname,lastname,owner" />" >Person-Test</a>
    </td><td>
  A simple one-step person editor. First-name, last-name and related articles.
  </td></tr>
  <tr><td>
      <a href="<mm:url referids="referrer" page="/mmbase/edit/wizard/jsp/list.jsp?wizard=lib/createimage&nodepath=images&fields=title,handle" /> " >Images</a>
  </td><td>
   You can also upload images with an editwizard. Here is shown how this can be done.
  </td></tr>
  <tr><td>
    <a href="<mm:url referids="referrer" page="/mmbase/edit/wizard/jsp/list.jsp?wizard=../samples/news&nodepath=news&fields=title,date,owner" />" >News</a>
    </td><td>
   An editor for news articles. In the one step you can create or add a news article and relate people and images to it.
  </td></tr>
  </table>
  <hr />
  <a class="navigate" href="../"><img alt="back" src="<mm:url page="/mmbase/style/images/back.png" />" /></a><br />
 </mm:cloud>
</form>
</body>
</html>