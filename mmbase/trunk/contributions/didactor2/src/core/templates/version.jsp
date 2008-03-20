<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
<html>
  <head>
    <title>Didactor version.</title>
    <link rel="stylesheet" href="mmbase/style/css/mmbase.css" type="text/css" />
  </head>
  <body>
    <p><b>Didactor version:</b> 2.3.0</p>
    <p><b>CVS version:</b> $Name: not supported by cvs2svn $ </p>
    <p><b>Build name:</b> ${war.name}</p>
    <p><b>Build date:</b> ${war.time}</p>
    <p><b>Components:</b> ${components}</p>
    <p><b>Provider:</b> ${provider}</p>
    <p><b>Education:</b> ${education}</p>
  </body>
</html>
</jsp:root>
