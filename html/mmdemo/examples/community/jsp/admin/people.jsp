<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
<html>
<head>
<title>Forum Demo : Add People</title>
</head>
<body>

<p>This form is used to add people to the forum.
You need to enter at least a name, a logon name, and a password.
The page will then add a People and a Chatter object and relate these together.
(Note: this demo application uses the People and Chatter builder. However, you can also make use of the User object or a custom object of your own.)
</p>
<p>The logon name and password are used to enter the Forum (they do not give access to the MMBase editors or admin functions)</p>

<blockquote>
<form method="post" action="postpeople.jsp">
<p>First name :<br />
<input type="text" name="firstname" size="32" maxlength="32" value="" />
</p>
<p>Last name :<br />
<input type="text" name="lastname" size="32" maxlength="32" value="" />
</p>
<p>Email address :<br />
<input type="text" name="email" size="32" maxlength="32" value="" />
</p>
<hr />
<p>Login name :<br />
<input type="text" name="login" size="30" maxlength="30" value="" />
</p>
<p>Password :<br />
<input type="password" name="password" size="40" maxlength="40" value="" />
</p>
<input type="submit" name="action" value="Add">
<input type="reset" value="Reset">
</form>
</blockquote>
</body></html>