<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" 
%><html><head><script language="JavaScript">
window.setInterval("call()", 10000, "JavaScript");
function call(){location="chatwho.jsp"; }</script>
</head><body><mm:cloud><b>Online</b><hr />
<mmcommunity:who channel="Chat" ><mm:field name="gui()" /><br /></mmcommunity:who>
</mm:cloud></body></html>
