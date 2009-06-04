<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:context id="chats">
<mm:cloud>
<html>
<mm:node number="Chat" id="channel">
<head>
<title><mm:field name="title" /></title>
</head>
</mm:node>

<mm:import externid="chatter" from="session" />
<mm:notpresent referid="chatter" >
<body  onLoad="document.chatsform.login.focus();">
<form action="login.jsp" name="chatsform" method="post">
   <table>
   <tr><td>Login:</td><td><input type="text" name="login"   size="10" /></td></tr>
   <tr><td>Password:</td><td><input type="password" name="pwd" size="10" /></td></tr>
   </table>
<input type="submit" value="start chat">
</p>
</form>
</body>
</mm:notpresent>

<mm:present referid="chatter">
<frameset rows="0,*,60" border="1" frameborder="0" framespacing="0" >
<frame name="chatloader" scrolling="no" target="chatloader" src="<mm:url page="chatloader.jsp"/>" marginwidth="1" marginheight="0">
 <frameset cols="120,*" border="1" frameborder="1" framespacing="0" >
  <frame name="chatwho" src="chatwho.jsp" marginwidth="1" marginheight="0" />
  <frame name="chatbox" src="chatbox.jsp" marginwidth="1" marginheight="0" />
 </frameset>
<frame name="chatline" src="chatline.jsp" scrolling="no" marginwidth="1" marginheight="0" />
</frameset>
</mm:present>

</mm:cloud>
</mm:context>
</html>
