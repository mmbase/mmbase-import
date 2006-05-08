<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
  </head>

  <body>
    <h1></h1>
<% 
    java.util.Enumeration e =request.getHeaderNames();
while(e.hasMoreElements()) {
    String header = (String) e.nextElement();
    out.println(header + ":" + request.getHeader(header) + "<br />");
}
%>
  </body>
</html>
