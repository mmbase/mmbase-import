<html>
<head>
	<title>EditWizard samples (dutch version)</title>
	<link rel="stylesheet" type="text/css" href="../style.css" />
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
   <!-- Very straightforward example -->
</head>
<body>
<form>
	<h1>Editwizard - samples, 'dutch' version</h1>
  <p>
   This example uses the default editwizard XSL's, much like the 'simple' editwizard example.
   It also overrides the 'prompts' xsl stylesheet, which in this case means that all feedback of the
   editwizard is shown in dutch instead of the standard english.<br />
   You can make your own 'international' version of teh editors by editing the xsl/prompts.xsl file, replacing 
   the values in there with prompts and tooltips in your own preferered lanuage.
  </p>
	<table>
  <tr><td>
	<a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/people&nodepath=people&fields=firstname,lastname,owner" />" >Person-Test</a>
  </td><td> 
  A simple one-step person editor. First-name, last-name and related articles.
  </td></tr>
  <tr><td>
	  <a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/imageupload&nodepath=images&fields=title" /> " >Images</a>
  </td><td> 
   You can also upload images with an editwizard. Here is shown how this can be done.
  </td></tr>
  <tr><td>
	<a href="<mm:url page="/mmapps/editwizard/jsp/list.jsp?wizard=samples/news&nodepath=news&fields=title,owner" />" >News</a>
    </td><td> 
   An editor for news articles. In the one step you can create or add a news article and relate people and images to it.
  </td></tr>
  </table>
  <hr />

  <a href="../index.html">back</a>
 
</form>
</body>
</html>
