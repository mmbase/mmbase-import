<%
   session.removeAttribute("chatter");
   session.removeAttribute("chattername");
   session.removeAttribute("chatteremail");
   Cookie cookie=new Cookie("mmbase_chatter","0");
   cookie.setMaxAge(-1);
   response.addCookie(cookie);
%>
<html>
<head>
<title>Forum Demo: Logoff</title>
</head>
<body>
<blockquote>
<p>U bent nu uitgelogd.</p>
<p>Terug naar <a href="forum.jsp">het MMBase Forum</a></p>
</blockquote>
</body></html>