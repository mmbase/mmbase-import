<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<html>
  <head>
    <title>503 temporary unavailable</title>
    <%@include file="meta.jsp"%>
  </head>
  <body >
    <h1>503 This web-site is temporary unavailable</h1>
    <h2><%=org.mmbase.Version.get()%></h2>
    <p>
      This web-site is currently unavailable. Please come back in a few minutes.
    </p>
    <p>
      If you are the administrator of this site, and this message does not dissappear, please check
      the mmbase log, which probably contains an indication of the reason why MMBase is not yet
      successfully started.
    </p>
  </body>
</html>

