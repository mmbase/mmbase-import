<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html>
<mm:cloud>
<mm:import externid="channel" required="true" />
<mm:import externid="chatter" from="session" />
<mm:notpresent referid="chatter" >

<body  onLoad="document.chatsform.login.focus();">
<form action="<mm:url page="login.jsp" />" name="chatsform" method="post">
<input type="hidden" name="channel" value="<mm:write referid="channel" />">
<p>Login : <input type="text" name="login" size="10">
<br />
Password : <input type="password" name="pwd" size="10">
<input type="submit" value="start chat">
</p>
</form>
</body>

</mm:notpresent>
<mm:present referid="chatter" >

<frameset rows="1,*,60" border="1" frameborder="0" framespacing="0" >
<frame name="chatloader" scrolling="no" noresize target="chatloader" src="<mm:url page="chatloader.jsp" referids="channel" />"
  marginwidth="1" marginheight="0">
 <frameset cols="120,*" border="1" frameborder="1" framespacing="0" >
  <frame name="chatwho" src="<mm:url page="chatwho.jsp" referids="channel" />" marginwidth="1" marginheight="0" />
  <frame name="chatbox" src="<mm:url page="chatbox.jsp" referids="channel" />" marginwidth="1" marginheight="0" />
 </frameset>
<frame name="chatline" src="<mm:url page="chatline.jsp" referids="channel" />" scrolling="no" marginwidth="1" marginheight="0" />
</frameset>

</mm:present>
</mm:cloud>
</html>
