<html>
<head>
	<title>EditWizard samples</title>
	<link rel="stylesheet" type="text/css" href="../style.css" />
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
   <!-- Very straightforward example -->
</head>
<body>
<form>
	<h1>Editwizard - samples</h1>

  
	<br />
	<a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/people&nodepath=people&fields=firstname,lastname,owner" />" >Person-Test</a><br/>
	<a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/imageupload&nodepath=images&fields=title" /> " >Images</a><br/>
	<a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/news&nodepath=news&fields=title,owner" />" >News</a><br/>
  <hr />

  <a href="../index.html">back</a>
 
</form>
</body>
</html>
