<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<mm:cloud>
<mm:import externid="channel" required="true" />
<mm:import externid="chatter" from="session" />
<mm:import externid="chattername" from="session" />
<mm:import externid="body" />

<mmcommunity:connection channel="Chat" user="${chatter}" action="stillactive" />
<mm:present referid="body">
 <mmcommunity:post>
  <mm:setfield name="username"><mm:write referid="chattername"/></mm:setfield>
  <mm:setfield name="user"><mm:write referid="chatter"/></mm:setfield>
  <mm:setfield name="channel"><mm:write referid="channel"/></mm:setfield>
  <mm:setfield name="body"><mm:write referid="body"/></mm:setfield>
 </mmcommunity:post>
<script language="JavaScript">
    top.chatloader.call();
</script>
</mm:present>
</head>
<body onLoad="document.chatlineform.body.focus();">
<a name="post"></a>
<form name="chatlineform" method="post" action="<mm:url page="chatline.jsp" referids="channel" />">
<input name="body" size="80" />
<input type="submit" name="action" value="OK">
<a href="<mm:url page="logoff.jsp" referids="channel" />" target="_top">Log off!</a>
</form>
</mm:cloud>
</body></html>
