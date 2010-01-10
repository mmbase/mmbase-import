<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html" expires="0" escaper="none"><html>
  <mm:import externid="refer" required="true" />
  <mm:import externid="loginsessionname" from="parameters" ></mm:import>
  <head>
    <mm:url id="referurl" page="$refer" write="false" />
    <meta http-equiv="refresh" content="0; url=<mm:write referid="referurl" />" />
    <title>Logout, redirecting to <mm:write referid="referurl" /></title>
  </head>
  <mm:cloud method="logout" sessionname="$loginsessionname" />
  <body>
    <h1>Redirecting to <a href="<mm:write referid="referurl" />"><mm:write referid="referurl" /></a></h1>
  </body>
</html>
</mm:content>