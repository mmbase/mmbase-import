<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page session="false" %>
<%-- Place this JSP on the A-Select server and  point to it in <mmbase-app>/WEB-INF/config/security/config.xml  --%>
<html>
  <head>
  <title>ASelect Server logout- page</title>
</head>
<body>
<% String url = request.getParameter("app_url"); 
   Cookie cookie = new Cookie("aselect_credentials", null);
   cookie.setMaxAge(0);
   response.addCookie(cookie);
   if (url != null) {
     response.sendRedirect(url);
  }
%>
<h1>You were logged out</h1>
<p>
  
</p>
</body>
</html>