<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  </head>
  <body>
    <mm:import externid="referrer">cloud2.jsp</mm:import>
    <mm:import externid="reason">please</mm:import>
    <h1>Login</h1>
    <mm:write referid="reason">      
      <mm:compare value="please" inverse="true">
        <p>
          <font color="red"><mm:write /></font>
        </p>
      </mm:compare>
    </mm:write>
    <table>
      <form method="post" action="<mm:write referid="referrer" />" >
        <input type="hidden" name="authenticate" value="name/password" />
        <tr><td>Name:</td><td><input type="text" name="username"></td></tr>
        <tr><td>Password</td><td><input type="password" name="password"></td></tr>
        <tr><td /><td><input type="submit" name="command" value="login"></td></tr>
      </form>
    </table>
  </body>
  <hr />
   <a href="<mm:url page="/mmexamples/taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<hr />
</html>
