<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<mm:cloud>
<mm:node number="Chat" id="channel">
</head>
<body onLoad="document.chatlineform.body.focus();">
<a name="post"></a>
<form name="chatlineform" method="post" action="chatline.jsp">
<input name="body" size="80" /><input type="submit" name="action" value="OK">
<a href="logoff.jsp" target="_top">Leave</a>  <font color="red">XX</font>
</form>
</mm:node>
</mm:cloud>
</body></html>
