<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud>
<mm:import externid="channel" required="true" />
<%
   String chatter=(String)session.getAttribute("chatter");
   session.removeAttribute("chatter");
   session.removeAttribute("chattername");
   Cookie cookie=new Cookie("mmbase_chatter","0");
   cookie.setMaxAge(-1);
   response.addCookie(cookie);
%>
<mmcommunity:connection channel="${channel}" user="<%=chatter%>" action="leave"/>
<html><body>
<blockquote>
<p>U bent nu uitgelogd.</p>
<p>Terug naar <a href="<mm:url page="chats.jsp" referids="channel" />">MMBase Chat</a></p>
</blockquote>
</body></html>
</mm:cloud>
