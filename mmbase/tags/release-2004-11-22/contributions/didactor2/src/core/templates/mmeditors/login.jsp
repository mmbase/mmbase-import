<%@ include file="page_base.jsp"  
%><mm:content type="text/html" language="$config.lang" expires="0">
<mm:write referid="style" escape="none" />
<title>Login</title>
</head>
<body class="basic">
  <h2>Login</h2>
  <mm:import externid="reason">please</mm:import>
  <mm:import externid="referrer">search_node.jsp</mm:import>
  <mm:compare referid="reason" value="failed">
    <p class="failed">
      Failed to log in. Try again.
    </p>
  </mm:compare>
  <table>
    <form method="post" action="<mm:write referid="referrer" />" >
    <tr><td>Name:</td><td><input type="text" name="username"></td></tr>
    <tr><td>Password</td><td><input type="password" name="password"></td></tr>
    <tr><td>Authenticate:</td><td><input type="text" name="authenticate" value="name/password"></td></tr>
    <tr><td /><td><input type="submit" name="command" value="login"></td></tr>
  </form>
</table>
<%@ include file="footfoot.jsp"  %>
</mm:content>