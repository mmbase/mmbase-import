<%@ page errorPage="chatlineerror.jsp" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<mm:cloud>
<mm:import externid="chatter" from="session"  required="true" />
<mm:import externid="chattername" from="session" required="true" />
<mm:import externid="body" from="parameters" />

<mm:node number="Chat" id="channel">
<mmcommunity:connection channel="Chat" user="${chatter}" action="stillactive" />
<mm:present referid="body">
 <mmcommunity:post>
  <mm:setfield name="username"><mm:write referid="chattername"/></mm:setfield>
  <mm:setfield name="user"><mm:write referid="chatter"/></mm:setfield>
  <mm:setfield name="channel"><mm:field name="number" node="channel"/></mm:setfield>
  <mm:setfield name="body"><mm:write referid="body"/></mm:setfield>
 </mmcommunity:post>
<script language="JavaScript">
 <mmcommunity:tree thread="Chat" max="1" directions="UP"  fields="sequence" >
  parent.frames.chatloader.location="chatloader.jsp?startnode=<mm:field name="sequence" />";
  </mmcommunity:tree>
</script>
</mm:present>
</head>
<body onLoad="document.chatlineform.body.focus();">
<a name="post"></a>
<form name="chatlineform" method="post" action="chatline.jsp">
<input name="body" size="80" /><input type="submit" name="action" value="OK">
<a href="logoff.jsp" target="_parent">Leave</a>
</form>
</mm:node>
</mm:cloud>
</body></html>
