<html>
<head>
    <title>EditWizard samples (dutch version)</title>
    <link rel="stylesheet" type="text/css" href="../style.css" />
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
   <!-- Very straightforward example -->
  <mm:import externid="language">nl</mm:import>
  <mm:import id="ew">/mmbase/edit/wizard/jsp</mm:import>
  <mm:import id="referrer"><%=new  java.io.File(request.getServletPath())%>?language=<mm:write referid="language" /></mm:import>
</head>
<body>
<form>
<mm:cloud rank="basic user" sessionname="" >
  <mm:write referid="language" jspvar="lang" vartype="string" >
    <h1>Editwizard - samples, '<%= new java.util.Locale(lang, "").getDisplayLanguage(java.util.Locale.US)%>' version</h1>
  </mm:write>
  <p>
   This example uses the default editwizard XSL's, much like the
   'simple' editwizard example. It does however orders the editwizards
   to present as much in dutch as possible by passing the
   'language=nl' argument.
  </p>
  <p>
   Support for other languages can be added in
   [editwizard-home]/data/i18n, and of course also in the builder
   xml's. Texts in the  editwizard-xml's can also be specified
   in more than one language by use of the 'xml:lang' attribute on the
   elements for which it would be logical.
  </p>
    <table>
  <tr><td>
    <a href="<mm:url referids="referrer,language" page="$ew/list.jsp?wizard=../samples/people&nodepath=people&fields=firstname,lastname,owner" />" >Person-Test</a>
  </td><td>
  A simple one-step person editor. First-name, last-name and related articles.
  </td></tr>
  <tr><td>
      <a href="<mm:url referids="referrer,language" page="$ew/list.jsp?wizard=../samples/imageupload&nodepath=images&fields=title" /> " >Images</a>
  </td><td>
   You can also upload images with an editwizard. Here is shown how this can be done.
  </td></tr>
  <tr><td>
    <a href="<mm:url referids="referrer,language" page="$ew/list.jsp?wizard=../samples/news&nodepath=news&fields=title,date,owner" />" >News</a>
    </td><td>
   An editor for news articles. In the one step you can create or add a news article and relate people and images to it.
  </td></tr>
  </table>
  <hr />
  <mm:cloudinfo type="user" /> (<mm:cloudinfo type="rank" />)
  <hr />
  <a class="navigate" href="../"><img alt="back" src="<mm:url page="/mmbase/style/images/back.png" />" /></a><br />
  <a href="index.jsp?language=<%=java.util.Locale.getDefault().getLanguage()%>">default</a><br />
  <a href="index.jsp?language=en">english</a><br />
  <a href="index.jsp?language=nl">dutch</a><br />
  <a href="index.jsp?language=fr">french</a><br />
  <a href="index.jsp?language=it">italian</a><br />
  <a href="index.jsp?language=eo">esperanto</a><br />
  <a href="mailto:editwizard@meeuw.org">Offer your help to improve/add support for your language</a><br />
</mm:cloud>
</form>
</body>
</html>
