<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html>
<mm:cloud>
<%
    String chatter=(String)session.getAttribute("chattername");
    if (chatter==null) {
%>
<body  onLoad="document.chatsform.chattername.focus();">
<form action="login.jsp" name="chatsform" method="post">
<p>Login : <input type="text" name="login" size="10">
<br />
Password : <input type="password" name="pwd" size="10">
<input type="submit" value="start chat">
</p>
</form>
</body>
<%  } else { %>
<frameset rows="1,*,60" border="1" frameborder="0" framespacing="0" >
<frame name="chatloader" scrolling="no" noresize target="chatloader" src="chatloader.jsp"
  marginwidth="1" marginheight="0">
<frame name="chatbox" src="chatbox.jsp" marginwidth="1" marginheight="0" />
<frame name="chatline" src="chatline.jsp" scrolling="no" marginwidth="1" marginheight="0" />
</frameset>
<%  } %>
</mm:cloud>
</html>