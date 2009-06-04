<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<head>
   <title>cookies</title>
   <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<% 
 Cookie[]  cookies = request.getCookies();
 for (int i=0; i< cookies.length; i++) {
	 out.println(cookies[i].getName() + ":" + cookies[i].getValue() + "<br />"); 
	 out.println("age: " + cookies[i].getMaxAge() + "<br />");
	 cookies[i].setMaxAge(0);
	 response.addCookie(cookies[i]);
	 }
 
%>

</body>
</html>
