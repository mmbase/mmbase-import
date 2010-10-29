<%@ include file="page_base.jsp"
%><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0" jspvar="locale">
<mm:write referid="style" escape="none" />
<title>Login</title>
</head>
<body class="basic">
  <h2>Login: <span class="uri"><mm:escape>${config.uri}</mm:escape></span></h2>

  <mm:cloud sessionname="$config.session" method="logout" uri="$config.uri"/>

  <mm:include attributes="config.lang@language, config.country@country, config.session@sessionname"  page="login.p.jsp" />
  <%@ include file="footfoot.jsp"  %>
</mm:content>
