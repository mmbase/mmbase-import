<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<mm:cloud>
<mm:import externid="chatter" from="session" />
<mm:import externid="chattername" from="session" />

<mm:node number="Chat" id="channel">
<mmcommunity:connection channel="Chat" user="$chatter" action="leave" />
 <mmcommunity:post>
  <mm:setfield name="username"><mm:write referid="chattername"/></mm:setfield>
  <mm:setfield name="user"><mm:write referid="chatter"/></mm:setfield>
  <mm:setfield name="channel"><mm:field name="number" node="channel"/></mm:setfield>
  <mm:setfield name="body"> left the MMBase chat box.</mm:setfield>
 </mmcommunity:post>
</mm:node><%

   String chatter=(String)session.getAttribute("chatter");
   session.removeAttribute("chatter");
   session.removeAttribute("chattername");
   Cookie cookie=new Cookie("mmbase_chatter","0");
   cookie.setMaxAge(-1);
   response.addCookie(cookie);
%>
<html>
<body>
<p>You were logged of</p>
<p>Back to <a href="<mm:url page="chats.jsp" />">MMBase Chat</a></p>
<p>Back to <a href="<mm:url page="../community.jsp" />">Community example</a></p>
</body>
</html>
</mm:cloud>
