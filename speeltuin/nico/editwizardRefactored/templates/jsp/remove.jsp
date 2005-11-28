<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html" expires="0" escaper="none"><%
%><html>
<mm:import name="sendCommand" jspvar="sendCommand"/>
<mm:import name="objectNumber" jspvar="objectNumber"/>
<script language="javascript">
<mm:present referid="sendCommand">
try { // Mac IE doesn't always support window.opener.
window.opener.saveScroll();
window.opener.doSendCommand("<%=sendCommand%>","<%=objectNumber%>");
} catch (e) {}
</mm:present>
window.close();
</script>
</html>
