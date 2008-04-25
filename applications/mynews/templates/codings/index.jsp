<%@page   contentType="text/html;charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<head>
  <title>Codings examples</title>
  <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
</head>
<body>
<mm:content language="en" type="text/html" postprocessor="reducespace">
  <h1>Codings examples</h1>
  <p>
    These pages serve as a test-case and example on how to deal with character-encodings.

  </p>
  <ul>
    <li>
      Versions with use of pageEncoding (<a href="http://java.sun.com/products/jsp/syntax/1.2/syntaxref1210.html">a JSP 1.2 feature</a>):
      <a href="<mm:url page="default8.jsp" />">UTF-8</a>, <a href="<mm:url page="default1.jsp" />">ISO-8859-1</a>,
      ( <a href="<mm:url page="default8.jsp?postprocessor=none" />">UTF-8</a>, <a href="<mm:url page="default1.jsp?postprocessor=none" />">ISO-8859-1</a> without postprocessing)
      <p>
        Not supported by all app-servers. Works absolutely perfectly in tomcat 5. Newer tomcat 4 versions
        almost work, it does only not permit the pageEncoding on the 'atincluded' pages (to fix
        the error you must remove those).
      </p>
    </li>
    <li>
      Versions without use of pageEncoding, and without some other features:
      <a href="<mm:url page="legacy/default8.jsp" />">UTF-8</a>, <a href="<mm:url page="legacy/default1.jsp" />">ISO-8859-1</a>
      <p>
        Works at least partially in the app-servers I tested. (tomcat, orion)
      </p>
      <p>
        The post-processing of mm:content was disabled because orion messes up the encoding
        then. Orion > 2.0.2 will also work with UTF-8 (provided that included page is also UTF-8).
      </p>
    </li>
    <li>
      <a href="<mm:url page="poster.jsp" />">Simple tests of forms (without using database)</a>. Pushing the buttons should not result in a visual change.
    </li>
    <li>
      <a href="<mm:url page="poster1.jsp" />">Same thing, but now on a latin-1 page.</a>. You notice that non-recognized character are indeed not working.
    </li>
  </ul>
  <hr />
  <div class="link">
    <a href="<mm:url page=".." />"><img alt="back" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
  </div>
</mm:content>
<address><a href="mailto:Michiel.Meeuwissen@omroep.nl">miĥil'</a></address>
</body>
</html>




