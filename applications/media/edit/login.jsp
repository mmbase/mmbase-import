<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<?xml version="1.0" encoding="UTF-8"?>
<html>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<title>Login</title>
</head>
<body>
    <span class="kop">Login</span>
     <mm:import externid="reason">please</mm:import>
     <mm:import externid="referrer">search.jsp</mm:import>
      <mm:compare referid="reason" value="failed">
        <p class="failed">
          Failed to log in. Try again.
        </p>
      </mm:compare>
    <table>
      <form method="post" action="dologin.jsp" >
        <tr><td>Name:</td><td><input type="text" name="username"></td></tr>
        <tr><td>Password</td><td><input type="password" name="password"></td></tr>
        <!-- tr><td>Authenticate:</td><td><input type="text" name="authenticate" value="name/password"></td></tr-->
        <tr><td /><td><input type="submit" name="command" value="login"></td></tr>
      </form>
    </table>
</body>
</html>
