<html>
<head>
<title>Login</title>
<link href="../style/login.css" type="text/css" rel="stylesheet"/>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%></head>
<body>
  <mm:import externid="reason">please</mm:import>
  <mm:import externid="referrer" required="true" />
     <form method="post" action="<mm:write referid="referrer" />" >
    <table class="body">
    <tr><th colspan="2">Login</th></tr>
  <mm:compare referid="reason" value="failed">
     <tr><th class="failed" colspan="2">
       Failed to log in. Try again.
     </th></tr>
  </mm:compare>
       <tr><td>Name:</td><td><input type="text" name="username"></td></tr>
        <tr><td>Password</td><td><input type="password" name="password"></td></tr>
        <tr><td>Authenticate:</td><td><input type="text" name="authenticate" value="name/password"></td></tr>
        <tr><td /><td><input type="submit" name="command" value="login"></td></tr>
    </table>
      </form>
</body>
</html>

