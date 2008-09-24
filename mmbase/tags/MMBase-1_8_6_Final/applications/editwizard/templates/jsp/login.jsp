<html>
<head>
<title>Login</title>
<link href="../style/login.css" type="text/css" rel="stylesheet"/>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%></head
<mm:content type="text/html" expires="0" escaper="none">
<body>
  <h2>Login</h2>
  <mm:cloud method="logout" />
  <mm:include  page="login.p.jsp" />
  
</body>
</mm:content>
</html>

