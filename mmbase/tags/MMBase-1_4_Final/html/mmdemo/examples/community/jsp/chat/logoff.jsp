<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud>
<%
   Node chatternode=(Node)session.getAttribute("chatter");
   session.removeAttribute("chatter");
   session.removeAttribute("chattername");
   session.removeAttribute("chatteremail");
   Cookie cookie=new Cookie("mmbase_chatter","0");
   cookie.setMaxAge(-1);
   response.addCookie(cookie);
   Module community= LocalContext.getCloudContext().getModule("communityprc");
   Node channelNode = cloud.getNodeByAlias("Chat");
   int channelnr = channelNode.getNumber();
   community.getInfo("CHANNEL-"+channelnr+"-LEAVE-"+chatternode.getNumber());
%>
<html><body>
<blockquote>
<p>U bent nu uitgelogd.</p>
<p>Terug naar <a href="chats.jsp">MMBase Chat</a></p>
</blockquote>
</body></html>
</mm:cloud>
